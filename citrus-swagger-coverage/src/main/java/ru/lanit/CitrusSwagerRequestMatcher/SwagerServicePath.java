package ru.lanit.CitrusSwagerRequestMatcher;

import org.springframework.http.HttpRequest;
import v2.io.swagger.models.Path;
import v2.io.swagger.models.Swagger;
import v2.io.swagger.parser.SwaggerParser;

import java.util.*;

public class SwagerServicePath {
    static {
        //  readSwagerInfo("swagger/petstore-api.json");
    }

    private static List<SwagerServicePath> swagerServicePaths;
    private static final String parametrRegEx = "\\{[\\w\\d]+?\\}";


    private static void readSwagerInfo(String path) {
        Swagger swagger = new SwaggerParser().read(path);
        String basePath = swagger.getBasePath();
        if (swagerServicePaths == null) {
            swagerServicePaths = new ArrayList<>(swagger.getPaths().size());
        } else {
            swagerServicePaths.clear();
        }
        for (Map.Entry<String, Path> sPath : swagger.getPaths().entrySet()) {
            swagerServicePaths.add(new SwagerServicePath(sPath.getKey(), basePath));
        }
        swagerServicePaths.sort(Comparator.comparingInt(o -> o.pathParamsCount));
    }


    public static PathUse matchPath(HttpRequest request) {
        Optional<SwagerServicePath> optionalSwagerServicePath = swagerServicePaths.stream()
                .filter(x -> !x.parametrizeblePath).filter(x -> x.isSuitablePath(request.getURI().getPath())).findAny();
        if (optionalSwagerServicePath.isPresent()) {
            System.out.println("Ура, мы нашли нужный путь в сваггере");
        } else {
            optionalSwagerServicePath = swagerServicePaths.stream().filter(x -> x.parametrizeblePath)
                    .filter(x -> x.isSuitablePath(request.getURI().getPath())).findAny();
            if (optionalSwagerServicePath.isPresent()) {
                System.out.println("Ура, мы нашли параметризуемый путь в свагере");
            } else {
                throw new RuntimeException("Крайне информативное исключение");
            }
        }

        SwagerServicePath servicePath = optionalSwagerServicePath.get();
        PathUse res = new PathUse(servicePath.path);
        String reqPath = request.getURI().getPath().split(servicePath.basePath)[1];
        String[] requestPathElements = reqPath.split("/");
        String[] swagerPathElements = servicePath.path.split("/");
        for (int i = 0; i < swagerPathElements.length; i++) {
            if (isPathParametrizeble(swagerPathElements[i])) {
                res.addParam(swagerPathElements[i].substring(1, swagerPathElements[i].length() - 1),
                        requestPathElements[i]);
            }
        }
        return res;
    }

    public final int pathParamsCount;

    protected String path;
    protected String basePath;
    protected boolean parametrizeblePath;


    private static boolean isPathParametrizeble(String path) {
        return path.replaceAll(parametrRegEx, "").length() != path.length();
    }

    private SwagerServicePath(String path, String basePath) {
        this.path = path;
        this.basePath = basePath;
        parametrizeblePath = isPathParametrizeble(path);
        pathParamsCount = path.length() - path.replaceAll("\\{", "").length();
    }

    private boolean isSuitablePath(String pathFromRequest) {
        if (basePath != null) {
            try {
                pathFromRequest = pathFromRequest.split(basePath)[1];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalStateException(String.format("В пути запроса %s не удалось определить базовый путь %s, указанный в swager", pathFromRequest, basePath));
            }
        }
        String[] requestPathElements = pathFromRequest.split("/");
        String[] swagerPathElements = path.split("/");
        if (requestPathElements.length != swagerPathElements.length)
            return false;
        for (int i = 0; i < swagerPathElements.length; i++) {
            if (isPathParametrizeble(swagerPathElements[i]))
                continue;
            if (!swagerPathElements[i].equals(requestPathElements[i])) {
                return false;
            }
        }
        return true;
    }
}
