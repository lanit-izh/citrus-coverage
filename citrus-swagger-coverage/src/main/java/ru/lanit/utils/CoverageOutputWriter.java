package ru.lanit.utils;

import v2.io.swagger.models.Swagger;

public interface CoverageOutputWriter {
    void write(Swagger swagger);
}
