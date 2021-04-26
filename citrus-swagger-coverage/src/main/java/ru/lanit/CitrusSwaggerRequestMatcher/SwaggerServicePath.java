package ru.lanit.CitrusSwaggerRequestMatcher;

import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import org.springframework.http.HttpRequest;

import java.util.*;

public class SwaggerServicePath {

    private static final String parametrRegEx = "\\{[\\w\\d]+?\\}";
    private static List<SwaggerServicePath> swaggerServicePaths;
    public final int pathParamsCount;
    protected String path;
    protected String basePath;
    protected boolean parametrizeblePath;

    private SwaggerServicePath(String path, String basePath) {
        this.path = path;
        this.basePath = basePath;
        parametrizeblePath = isPathParametrizeble(path);
        pathParamsCount = path.length() - path.replaceAll("\\{", "").length();
    }

    private static void readSwaggerInfo(String path) {
        Swagger swagger = new SwaggerParser().read(path);
        String basePath = swagger.getBasePath();
        if (swaggerServicePaths == null) {
            swaggerServicePaths = new ArrayList<>(swagger.getPaths().size());
        } else {
            swaggerServicePaths.clear();
        }
        for (Map.Entry<String, Path> sPath : swagger.getPaths().entrySet()) {
            swaggerServicePaths.add(new SwaggerServicePath(sPath.getKey(), basePath));
        }
        swaggerServicePaths.sort(Comparator.comparingInt(o -> o.pathParamsCount));
    }

    public static PathUse matchPath(HttpRequest request) {
        Optional<SwaggerServicePath> optionalSwaggerServicePath = swaggerServicePaths.stream()
                .filter(x -> !x.parametrizeblePath).filter(x -> x.isSuitablePath(request.getURI().getPath())).findAny();
        if (optionalSwaggerServicePath.isPresent()) {
            System.out.println("Ура, мы нашли нужный путь в сваггере");
        } else {
            optionalSwaggerServicePath = swaggerServicePaths.stream().filter(x -> x.parametrizeblePath)
                    .filter(x -> x.isSuitablePath(request.getURI().getPath())).findAny();
            if (optionalSwaggerServicePath.isPresent()) {
                System.out.println("Ура, мы нашли параметризуемый путь в свагере");
            } else {
                throw new RuntimeException("Крайне информативное исключение");
            }
        }

        SwaggerServicePath servicePath = optionalSwaggerServicePath.get();
        PathUse res = new PathUse(servicePath.path);
        String reqPath = request.getURI().getPath().split(servicePath.basePath)[1];
        String[] requestPathElements = reqPath.split("/");
        String[] swaggerPathElements = servicePath.path.split("/");
        for (int i = 0; i < swaggerPathElements.length; i++) {
            if (isPathParametrizeble(swaggerPathElements[i])) {
                res.addParam(swaggerPathElements[i].substring(1, swaggerPathElements[i].length() - 1),
                        requestPathElements[i]);
            }
        }
        return res;
    }

    private static boolean isPathParametrizeble(String path) {
        return path.replaceAll(parametrRegEx, "").length() != path.length();
    }

    private boolean isSuitablePath(String pathFromRequest) {
        if (basePath != null) {
            try {
                pathFromRequest = pathFromRequest.split(basePath)[1];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalStateException(String.format("В пути запроса %s не удалось определить базовый путь %s," +
                        " указанный в swagger", pathFromRequest, basePath));
            }
        }
        String[] requestPathElements = pathFromRequest.split("/");
        String[] swaggerPathElements = path.split("/");
        if (requestPathElements.length != swaggerPathElements.length)
            return false;
        for (int i = 0; i < swaggerPathElements.length; i++) {
            if (isPathParametrizeble(swaggerPathElements[i]))
                continue;
            if (!swaggerPathElements[i].equals(requestPathElements[i])) {
                return false;
            }
        }
        return true;
    }
}
