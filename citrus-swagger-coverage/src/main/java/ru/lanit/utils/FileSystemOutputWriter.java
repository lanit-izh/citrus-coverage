package ru.lanit.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.lanit.interfaces.CoverageOutputWriter;
import v2.io.swagger.models.Swagger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

public class FileSystemOutputWriter implements CoverageOutputWriter {
    private final Path outputDirectory;

    private final ObjectMapper mapper;

    public FileSystemOutputWriter(final Path outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.mapper = SwaggerCoverage2ModelJackson.createMapper();
    }

    private void createDirectories(final Path directory) {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new SwaggerCoverageWriteException("Could not create Swagger output directory", e);
        }
    }

    @Override
    public void write(Swagger swagger) {
        final String swaggerResultName = UUID.randomUUID().toString() + "-coverage.json";
        createDirectories(outputDirectory);
        Path file = outputDirectory.resolve(swaggerResultName);
        try (OutputStream os = Files.newOutputStream(file, CREATE_NEW)) {
            mapper.writeValue(os, swagger);
        } catch (IOException e) {
            throw new SwaggerCoverageWriteException("Could not write Swagger", e);
        }
    }
}
