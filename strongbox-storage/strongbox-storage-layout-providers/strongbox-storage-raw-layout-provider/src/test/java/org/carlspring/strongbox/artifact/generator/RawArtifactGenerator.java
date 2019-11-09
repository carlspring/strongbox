package org.carlspring.strongbox.artifact.generator;

import org.carlspring.strongbox.util.TestFileUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author Wojciech Pater
 */
public class RawArtifactGenerator
        implements ArtifactGenerator
{

    private Path basePath;
    private Path artifactPath;

    public RawArtifactGenerator(Path basePath)
    {
        super();
        this.basePath = basePath;
    }

    @Override
    public Path generateArtifact(String id,
                                 String version,
                                 long bytesSize)
            throws IOException
    {
        Files.createDirectories(basePath);
        artifactPath = basePath.resolve(Paths.get(id, version));
        Files.createDirectories(artifactPath.getParent());

        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(artifactPath, StandardOpenOption.CREATE)))
        {
            TestFileUtils.generateFile(out, bytesSize);
        }
        return artifactPath;
    }

    @Override
    public Path generateArtifact(URI uri,
                                 long bytesSize)
            throws IOException
    {
        Files.createDirectories(basePath);
        artifactPath = basePath.resolve(Paths.get(uri));
        Files.createDirectories(artifactPath.getParent());

        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(Paths.get(uri), StandardOpenOption.CREATE)))
        {
            TestFileUtils.generateFile(out, bytesSize);
        }
        return artifactPath;
    }
}
