package com.famaridon.vdoc.scoped.properties.test;

import com.famaridon.maven.scoped.properties.beans.FileDescriptor;
import com.famaridon.maven.scoped.properties.beans.ScopedPropertiesConfiguration;
import com.famaridon.maven.scoped.properties.beans.properties.Property;
import com.famaridon.maven.scoped.properties.exceptions.BuildPropertiesFilesException;
import com.famaridon.maven.scoped.properties.tools.ScopedProperties;
import com.famaridon.vdoc.scoped.properties.handler.beans.ConfigurationUtilityClassFileConfiguration;
import com.famaridon.vdoc.scoped.properties.handler.beans.ConfigurationUtilityClassPropertyConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;

/**
 * Created by famaridon on 19/05/2014.
 *
 * @author famaridon
 */

public class ConfigurationUtilityClassHandlerTest {

	public static final String CUSTOM_PROPERTIES_XML_FILE_NAME = "my-product-site.properties.xml";
	protected static File tempDirectory;
	protected static File tempDirectoryInput;
	protected static File tempDirectoryOutput;
	protected static File propertiesXml;
	protected static int inputFileCount = 0;

	protected static Properties properties = new Properties();

	@BeforeClass
	public static void inti() throws IOException, BuildPropertiesFilesException {
		// build a temp directory using java nio
		tempDirectory = new File("./target/test/");
		tempDirectory.mkdirs();
		tempDirectoryInput = new File(tempDirectory, "input");
		tempDirectoryInput.mkdir();
		tempDirectoryOutput = new File(tempDirectory, "output");
		tempDirectoryOutput.mkdir();

		// copy the resource my-product-site.properties.xml into user temps directory to test in real case
		// WARNING : if you copy other file all test should be updated
		propertiesXml = new File(tempDirectoryInput, CUSTOM_PROPERTIES_XML_FILE_NAME);
		try (FileOutputStream fileOutputStream = new FileOutputStream(propertiesXml)) {
			IOUtils.copy(ConfigurationUtilityClassHandlerTest.class.getClassLoader().getResourceAsStream("input/" + CUSTOM_PROPERTIES_XML_FILE_NAME), fileOutputStream);
			inputFileCount++;
		}

		// run the command
		ScopedPropertiesConfiguration.Builder configurationBuilder = new ScopedPropertiesConfiguration.Builder();
		configurationBuilder.appendOutputFolder(tempDirectoryOutput);
		configurationBuilder.appendPropertiesXmlFolder(tempDirectoryInput);
		configurationBuilder.appendTargetScope("production");
		configurationBuilder.appendHandlerPackages(Collections.singletonList("com.famaridon.vdoc.scoped.properties.handler"));
		ScopedProperties scopedProperties = new ScopedProperties(configurationBuilder.build());
		Set<File> outputFileSet = scopedProperties.buildPropertiesFiles();
		Assert.assertTrue(outputFileSet.size() == inputFileCount);

		// the output file name should be custom.properties
		// we can't compare file byte per byte because properties output the timestamp.
		File output = new File(tempDirectoryOutput, "custom.properties");
		try (FileInputStream inputStream = new FileInputStream(output)) {
			properties.load(inputStream);
		} catch (FileNotFoundException e) {
			Assert.fail("output file not found!");
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	//@AfterClass
	public static void clean() throws IOException {
		//FileUtils.deleteDirectory(tempDirectory);
	}

	@Test
	public void testSimpleProperty() throws JAXBException {

		JAXBContext jaxbContext = JAXBContext.newInstance(FileDescriptor.class, ConfigurationUtilityClassFileConfiguration.class, ConfigurationUtilityClassPropertyConfiguration.class);

		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);


		FileDescriptor fileDescriptor = new FileDescriptor();

		Property property = new Property();
		property.setName("name");

		fileDescriptor.getItems().add(property);
		ConfigurationUtilityClassFileConfiguration configuration = new ConfigurationUtilityClassFileConfiguration();
		configuration.setClassName("MyFisrtClass");
		fileDescriptor.getHandlersConfiguration().put("VDocUtilityClass", configuration);

		marshaller.marshal(fileDescriptor, System.out);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(fileDescriptor, out);

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		FileDescriptor parsed = (FileDescriptor) unmarshaller.unmarshal(new ByteArrayInputStream(out.toByteArray()));

		System.out.println(parsed.getHandlersConfiguration());

	}


}
