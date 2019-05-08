package org.carlspring.strongbox.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.carlspring.strongbox.configuration.StoragesConfigurationManager;
import org.carlspring.strongbox.storage.MutableStorage;
import org.carlspring.strongbox.storage.repository.MutableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.core.JsonProcessingException;

@ContextConfiguration(classes = LayoutRequestMappingTest.LayoutRequestMappingConfiguration.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = { LayoutRequestMappingTest.TestLayoutController.class }, secure = false)
public class LayoutRequestMappingTest
{

    private static final String TEST_LAYOUT_ALIAS = "test-layout-alias";
    private static final String ANOTHER_LAYOUT_ALIAS = "another-layout-alias";

    @Inject
    private MockMvc mockMvc;
    @Inject
    private StoragesConfigurationManager storagesConfigurationManager;
    @Inject
    private TestLayoutController testLayoutController;

    @BeforeEach
    public void setup()
    {
        MutableStorage storage = new MutableStorage("storage0");
        Mockito.when(storagesConfigurationManager.getStorage("storage0")).thenReturn(storage);

        MutableRepository testLayoutRepository = new MutableRepository("releases");
        testLayoutRepository.setStorage(storage);
        testLayoutRepository.setLayout(TEST_LAYOUT_ALIAS);
        Mockito.when(storagesConfigurationManager.getRepository("releases")).thenReturn(testLayoutRepository);

        MutableRepository anotherRepository = new MutableRepository("another-releases");
        anotherRepository.setStorage(storage);
        anotherRepository.setLayout(ANOTHER_LAYOUT_ALIAS);
        Mockito.when(storagesConfigurationManager.getRepository("another-releases")).thenReturn(anotherRepository);
    }

    @Test
    public void testLayoutRequestMapping()
        throws Exception
    {
        mockMvc.perform(get("/storages/storage0/releases/path/to/artifact"))
               .andDo(print())
               .andExpect(status().isOk());
        Mockito.verify(testLayoutController).download("storage0", "releases");
        
        mockMvc.perform(get("/storages/storage0/another-releases/path/to/artifact"))
               .andDo(print())
               .andExpect(status().isNotFound());
        Mockito.verifyZeroInteractions(testLayoutController);

    }

    @Configuration
    static class LayoutRequestMappingConfiguration
    {

        @Bean
        TestLayoutController testLayoutController()
        {
            return Mockito.mock(TestLayoutController.class);
        }

        @Bean
        StoragesConfigurationManager configurationManager()
        {
            return Mockito.mock(StoragesConfigurationManager.class);
        }

        @Bean
        WebMvcRegistrations webMvcRegistrations()
        {
            return new WebMvcRegistrations()
            {

                @Override
                public RequestMappingHandlerMapping getRequestMappingHandlerMapping()
                {
                    return new CustomRequestMappingHandlerMapping();
                }

            };
        }
    }

    @Controller
    @LayoutRequestMapping(TEST_LAYOUT_ALIAS)
    static interface TestLayoutController
    {

        @GetMapping(path = "/{storageId}/{repositoryId}/**")
        void download(@PathVariable(name = "storageId") String storageId,
                  @PathVariable(name = "repositoryId") String repositoryId)
            throws JsonProcessingException,
            IOException;

    }

}
