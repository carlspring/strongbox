package org.carlspring.strongbox.storage.validation.resource;

import org.apache.maven.artifact.Artifact;
import org.carlspring.strongbox.configuration.Configuration;
import org.carlspring.strongbox.configuration.ConfigurationManager;
import org.carlspring.strongbox.providers.ProviderImplementationException;
import org.carlspring.strongbox.providers.layout.LayoutProvider;
import org.carlspring.strongbox.providers.layout.LayoutProviderRegistry;
import org.carlspring.strongbox.providers.repository.RepositoryProviderRegistry;
import org.carlspring.strongbox.storage.repository.Repository;
import org.carlspring.strongbox.storage.ArtifactResolutionException;
import org.carlspring.strongbox.storage.ArtifactStorageException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.carlspring.strongbox.providers.layout.LayoutProviderRegistry.getLayoutProvider;

/**
 * @author mtodorov
 */
@Component("artifactOperationsValidator")
public class ArtifactOperationsValidator
{

    @Autowired
    private ConfigurationManager configurationManager;

    @Autowired
    private RepositoryProviderRegistry repositoryProviderRegistry;

    @Autowired
    private LayoutProviderRegistry layoutProviderRegistry;


    public ArtifactOperationsValidator()
    {
    }

    public void validate(String storageId,
                         String repositoryId,
                         String artifactPath)
            throws ArtifactResolutionException
    {
        checkStorageExists(storageId);
        checkRepositoryExists(storageId, repositoryId);
        checkArtifactPath(artifactPath);
    }

    public void checkStorageExists(String storageId)
            throws ArtifactResolutionException
    {
        if (storageId == null)
        {
            throw new ArtifactResolutionException("No storage specified.");
        }

        if (getConfiguration().getStorage(storageId) == null)
        {
            throw new ArtifactResolutionException("Storage " + storageId + " does not exist.");
        }
    }

    public void checkRepositoryExists(String storageId,
                                      String repositoryId)
            throws ArtifactResolutionException
    {
        if (repositoryId == null)
        {
            throw new ArtifactResolutionException("No repository specified.");
        }

        if (getConfiguration().getStorage(storageId).getRepository(repositoryId) == null)
        {
            throw new ArtifactResolutionException("Repository " + repositoryId + " does not exist.");
        }
    }

    public void checkArtifactPath(String artifactPath)
            throws ArtifactResolutionException
    {
        if (artifactPath == null)
        {
            throw new ArtifactResolutionException("No artifact path specified.");
        }
    }

    public void checkAllowsDeployment(Repository repository)
            throws ArtifactStorageException
    {
        if (!repository.allowsDeployment())
        {
            throw new ArtifactStorageException("Deployment of artifacts to " + repository.getType() +
                                               " repository is not allowed!");
        }
    }

    public void checkAllowsRedeployment(Repository repository,
                                        Artifact artifact)
            throws IOException,
                   ProviderImplementationException
    {
        LayoutProvider layoutProvider = getLayoutProvider(repository, layoutProviderRegistry);
        if (layoutProvider.containsArtifact(repository, artifact) && !repository.allowsDeployment())
        {
            throw new ArtifactStorageException("Re-deployment of artifacts to " + repository.getType() +
                                               " repository is not allowed!");
        }
    }

    public void checkAllowsDeletion(Repository repository)
            throws ArtifactStorageException
    {
        if (!repository.allowsDeletion())
        {
            throw new ArtifactStorageException("Deleting artifacts from " + repository.getType() +
                                               " repository is not allowed!");
        }
    }

    public Configuration getConfiguration()
    {
        return configurationManager.getConfiguration();
    }

}
