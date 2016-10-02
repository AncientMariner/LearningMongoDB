package org.xander.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import org.xander.service.CarService;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@ContextConfiguration(locations = {"classpath:/org/xander/service/applicationContext-service.xml",
                                   "classpath:/org/xander/model/applicationContext-dao.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class CarControllerTest {
    private MockMvc mockMvc;

    @InjectMocks private CarController carController;
    @Mock private CarService carService;
    @Autowired WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

//        this.mockMvc = MockMvcBuilders
//                .webAppContextSetup(this.webApplicationContext)
//                .dispatchOptions(true)
//                .build();

//        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//        viewResolver.setPrefix("/WEB-INF/");
//        viewResolver.setSuffix(".jsp");
        this.mockMvc = standaloneSetup(carController).build();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void initialPage() throws Exception {
        when(carService.getAll()).thenReturn(new ArrayList<>());
        MvcResult mvcResult = mockMvc
                .perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("all")).andReturn();

        assertThat("returned value is not an arrayList",
                (ArrayList) mvcResult.getModelAndView().getModel().get("cars"),
                isA(ArrayList.class));
        verify(carService).getAll();
        verifyNoMoreInteractions(carService);
    }

    @Test
    public void showAddForm() throws Exception {
    }

    @Test
    public void addContact() throws Exception {
    }

    @Test
    public void showEditForm() throws Exception {
    }

    @Test
    public void deleteContact() throws Exception {
    }
}