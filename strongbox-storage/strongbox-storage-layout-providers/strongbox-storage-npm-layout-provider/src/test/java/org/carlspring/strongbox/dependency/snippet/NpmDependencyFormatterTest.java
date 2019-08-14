package org.carlspring.strongbox.dependency.snippet;

import org.carlspring.strongbox.artifact.coordinates.NpmArtifactCoordinates;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

/**
 * @author carlspring
 * @author Pablo Tirado
 */
@Execution(CONCURRENT)
public class NpmDependencyFormatterTest
{
    private DependencySynonymFormatter formatter = new NpmDependencyFormatter();

    @Test
    public void testNpmDependencyGenerationWithoutScope()
    {
        NpmArtifactCoordinates coordinates = new NpmArtifactCoordinates();
        coordinates.setId("angular");
        coordinates.setVersion("1.6.7");

        String snippet = formatter.getDependencySnippet(coordinates);

        System.out.print(snippet);

        assertEquals("\"angular\" : \"1.6.7\"\n",
                     snippet,
                     "Failed to generate dependency!");
    }

    @Test
    public void testNpmDependencyGenerationWithScope()
    {
        NpmArtifactCoordinates coordinates = new NpmArtifactCoordinates();
        coordinates.setId("angular");
        coordinates.setVersion("1.6.7");
        coordinates.setScope("@carlspring");

        String snippet = formatter.getDependencySnippet(coordinates);

        System.out.print(snippet);

        assertEquals("\"@carlspring/angular\" : \"1.6.7\"\n",
                     snippet,
                     "Failed to generate dependency!");
    }

}
