package teste.backend.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(GameController.class)
@WebAppConfiguration
public class GameControllerUnitTests {

	@Autowired
	private MockMvc mockMvc;

	   
	@MockBean
	private RestTemplate restTemplateMock;
	

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private final static String baseUri = "/games";

	@Before
	public void setup() {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.webApplicationContext);
		this.mockMvc = builder.build();
	}

	@Test
	public void shouldReturn200FileOk() throws Exception {

	    MockMultipartFile file = new MockMultipartFile("file", "test.log",
                null, "00:00 Init Game".getBytes());

		MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.fileUpload(baseUri+"/uploadFile")
                                      .file(file);
		this.mockMvc.perform(builder).andExpect(status().is(200));
	}
	
	@Test
	public void shouldReturn400InvalidExtension() throws Exception {

	    MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                null, "00:00 Init Game".getBytes());

		MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.fileUpload(baseUri+"/uploadFile")
                                      .file(file);
		this.mockMvc.perform(builder).andExpect(status().is(400));
	}
	
	@Test
	public void shouldReturn400EmptyFile() throws Exception {
		
		byte[] b = {};
	    MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                null, b);

		MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.fileUpload(baseUri+"/uploadFile")
                                      .file(file);
		this.mockMvc.perform(builder).andExpect(status().is(400));
	}
	
	

}
