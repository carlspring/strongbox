package org.carlspring.strongbox.web;

import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.carlspring.strongbox.configuration.StoragesConfigurationManager;
import org.carlspring.strongbox.storage.Storage;
import org.carlspring.strongbox.storage.repository.Repository;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

/**
 * @author sbespalov
 */
public class LayoutRequestCondition extends AbstractRequestCondition<LayoutRequestCondition>
{

    private final String layout;
    private final StoragesConfigurationManager configurationManager;

    public LayoutRequestCondition(StoragesConfigurationManager configurationManager,
                                    String layout)
    {
        this.layout = layout;
        this.configurationManager = configurationManager;
    }

    @Override
    public LayoutRequestCondition combine(LayoutRequestCondition other)
    {
        return this;
    }

    @Override
    public LayoutRequestCondition getMatchingCondition(HttpServletRequest request)
    {
        String servletPath = request.getPathInfo();
        if (!servletPath.startsWith("/storages"))
        {
            return null;
        }

        String[] pathParts = servletPath.split("/");
        if (pathParts.length < 4)
        {
            return null;
        }

        String storageId = pathParts[2];
        String repositoryId = pathParts[3];
        Storage storage = configurationManager.getStorage(storageId);
        if (storage == null)
        {
            return null;
        }
        Repository repository = configurationManager.getRepository(repositoryId);
        if (repository == null)
        {
            return null;
        }

        String requestedLayout = repository.getLayout();
        if (!layout.equals(requestedLayout))
        {
            return null;
        }

        return this;
    }

    @Override
    public int compareTo(LayoutRequestCondition other,
                         HttpServletRequest request)
    {
        return 1;
    }

    @Override
    protected Collection<?> getContent()
    {
        return Collections.singleton(layout);
    }

    @Override
    protected String getToStringInfix()
    {
        return layout;
    }

}
