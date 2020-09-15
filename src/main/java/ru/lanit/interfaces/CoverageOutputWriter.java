package ru.lanit.interfaces;

import v2.io.swagger.models.Swagger;

public interface CoverageOutputWriter {
    void write(Swagger swagger);
}
