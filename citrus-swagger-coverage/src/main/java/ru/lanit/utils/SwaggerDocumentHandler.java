package ru.lanit.utils;

import v2.io.swagger.models.Swagger;
import v2.io.swagger.parser.SwaggerParser;

import java.util.*;
import java.util.stream.Collectors;

public class SwaggerDocumentHandler {

    public static List<String> getSwaggerPartsOfPathToCompare(String swaggerLocale, int countPartsOfPathToCompare) {

        Swagger swagger = new SwaggerParser().read(swaggerLocale);
        List<String> paths = new ArrayList<String>(swagger.getPaths().keySet())
                .stream()
                .map(x -> x.replaceFirst("/", ""))
                .collect(Collectors.toList());
        List<String> comparesValues = new ArrayList<>();

        for (int i = 0; i < paths.size(); i++) {
            StringBuilder comparesValue = new StringBuilder();
            if (countPartsOfPathToCompare == 1) {
                comparesValue.append(paths.get(i).split("/")[0]);
                comparesValues.add(comparesValue.toString());
                continue;
            }
            String[] partsOfPath = paths.get(i).split("/");
            if (partsOfPath.length <= countPartsOfPathToCompare) {
                for (int j = 0; j < partsOfPath.length; j++) {
                    comparesValue.append(partsOfPath[j]).append("/");
                }
                if (comparesValue.toString().endsWith("/")) {
                    comparesValue.deleteCharAt(comparesValue.lastIndexOf("/"));
                }
                comparesValues.add(comparesValue.toString().split("\\{")[0]);
            } else {
                for (int k = 0; k < countPartsOfPathToCompare; k++) {
                    comparesValue.append(partsOfPath[k]).append("/");
                }
                comparesValues.add(comparesValue.toString().split("\\{")[0]);
            }
        }

        ArrayList<String> distinctComparesValues = new ArrayList<>(new HashSet<>(comparesValues));
        Collections.sort(distinctComparesValues, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.split("/").length - o1.split("/").length;
            }
        });
        return distinctComparesValues;
    }


    public static String getSwaggerPath(String userPath, List<String> swaggerPartsOfPathToCompare) {

        for (int i = 0; i < swaggerPartsOfPathToCompare.size(); i++) {
            if (userPath.contains(swaggerPartsOfPathToCompare.get(i))) {
                String[] parts = userPath.split(swaggerPartsOfPathToCompare.get(i));
                return parts.length < 2
                        ? "/" + swaggerPartsOfPathToCompare.get(i)
                        : "/" + swaggerPartsOfPathToCompare.get(i) + parts[parts.length - 1];
            }
        }
        return userPath;
    }
}
