package ru.lanit.utils;

import v2.io.swagger.models.Swagger;
import v2.io.swagger.parser.SwaggerParser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class SwaggerDocumentHandler {

    private static SwaggerParser swaggerParser;
    private static List<String> distinctComparesValues;

    public static void setSwaggerParser(SwaggerParser swaggerParser) {
        SwaggerDocumentHandler.swaggerParser = swaggerParser;
    }

    public static void setDistinctComparesValues(List<String> distinctComparesValues) {
        SwaggerDocumentHandler.distinctComparesValues = distinctComparesValues;
    }

    private static SwaggerParser getSwaggerInstance() {
        if (swaggerParser == null) {
            setSwaggerParser(new SwaggerParser());
            return swaggerParser;
        }
        return swaggerParser;
    }

    public static List<String> getSwaggerPartsOfPathToCompare(String swaggerLocale, int countPartsOfPathToCompare) {

        if (distinctComparesValues == null) {
            Swagger swagger = getSwaggerInstance().read(swaggerLocale);

            if (countPartsOfPathToCompare <= 0) {
                return null;
            }

            List<String> paths = new ArrayList<>(swagger.getPaths().keySet())
                    .stream()
                    .map(x -> x.replaceFirst("/", ""))
                    .collect(Collectors.toList());
            List<String> comparesValues = new ArrayList<>();

            for (String path : paths) {
                StringBuilder comparesValue = new StringBuilder();
                String[] partsOfPath = path.split("/");

                if (partsOfPath.length <= countPartsOfPathToCompare) {
                    for (String s : partsOfPath) {
                        comparesValue.append(s).append("/");
                    }
                } else {
                    for (int k = 0; k < countPartsOfPathToCompare; k++) {
                        comparesValue.append(partsOfPath[k]).append("/");
                    }
                }
                if (comparesValue.toString().endsWith("/")) {
                    comparesValue.deleteCharAt(comparesValue.lastIndexOf("/"));
                }
                comparesValues.add(("/" + comparesValue.toString()).split("\\/\\{")[0]);
            }

            distinctComparesValues = new ArrayList<>(new HashSet<>(comparesValues));
            distinctComparesValues.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o2.split("/").length - o1.split("/").length;
                }
            });
            setDistinctComparesValues(distinctComparesValues);
        }
        return distinctComparesValues;
    }


    public static String getSwaggerPath(String userPath, List<String> swaggerPartsOfPathToCompare) {

        String tempUserPath = userPath.split("(\\?|%3F)")[0];
        String pathWithoutQueryParams = tempUserPath.endsWith("/")
                ? tempUserPath.substring(0, tempUserPath.length() - 1)
                : tempUserPath;

        if (swaggerPartsOfPathToCompare == null) {
            return pathWithoutQueryParams;
        }

        for (String s : swaggerPartsOfPathToCompare) {
            if (pathWithoutQueryParams.contains(s)) {
                String pathBeginWithSlash = pathWithoutQueryParams.startsWith("/")
                        ? pathWithoutQueryParams
                        : "/" + pathWithoutQueryParams;

                String[] parts = pathBeginWithSlash.split(s);
                return parts.length < 2
                        ? s
                        : parts[0].equals("")
                        ? parts[1]
                        : s + parts[parts.length - 1];
            }
        }
        return pathWithoutQueryParams;
    }
}
