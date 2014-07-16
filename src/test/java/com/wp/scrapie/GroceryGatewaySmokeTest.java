package com.wp.scrapie;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.junit.Test;

import com.meterware.pseudoserver.PseudoServlet;
import com.meterware.pseudoserver.WebResource;

public class GroceryGatewaySmokeTest extends JsLoader {

	@Test
	public void testGroceryGateway() throws Exception {
		setupLogin();
		setupTestFiles();
		StringWriter writer = run("src/test/js/groceryGateway.js");
		StringWriter newWriter = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		String[] rows = writer.getBuffer().toString().split("\n");
		for (String row : rows) {
			ObjectWriter pretty = mapper.writer().withDefaultPrettyPrinter();
			pretty.writeValue(newWriter,
					mapper.readValue(row.trim(), Map.class));
			newWriter.append("\n");
		}
		assertEquals("{\n" + 
				"  \"Img\" : [ \"http://www.grocerygateway.com/sites/images/medium/M100505.jpg\" ],\n" + 
				"  \"Price\" : [ \"$19.99\" ],\n" + 
				"  \"Size\" : [ \"7\\\"\" ],\n" + 
				"  \"Title\" : [ \"Ceramic Rose Garden\" ]\n" + 
				"}\n" + 
				"{\n" + 
				"  \"Carbohydrates\" : [ \"0g\" ],\n" + 
				"  \"Cholesterol\" : [ \"0mg\" ],\n" + 
				"  \"Comments\" : [ \"Bertolli Extra Virgin Olive Oil\" ],\n" + 
				"  \"Country of Origin\" : [ \"Canada\" ],\n" + 
				"  \"Fat\" : [ \"14g\" ],\n" + 
				"  \"Img\" : [ \"http://www.grocerygateway.com/sites/images/medium/M1273.jpg\" ],\n" + 
				"  \"Ingredients\" : [ \"100% extra virgin olive oil.\" ],\n" + 
				"  \"Monounsaturates\" : [ \"11g\" ],\n" + 
				"  \"Polyunsaturates\" : [ \"1g\" ],\n" + 
				"  \"Potassium\" : [ \"0mg\" ],\n" + 
				"  \"Price\" : [ \"$7.99\", \"$9.99\" ],\n" + 
				"  \"Size\" : [ \"1L\", \"500mL\" ],\n" + 
				"  \"Sodium\" : [ \"0mg\" ],\n" + 
				"  \"Title\" : [ \"Bertolli Extra Virgin Olive Oil\" ]\n" + 
				"}\n" + 
				"{\n" + 
				"  \"Img\" : [ \"http://www.grocerygateway.com/sites/images/medium/M68494.jpg\" ],\n" + 
				"  \"Price\" : [ \"$12.00\" ],\n" + 
				"  \"Size\" : [ \"Package of 3\" ],\n" + 
				"  \"Title\" : [ \"Longos Chicken Souvlaki\" ]\n" + 
				"}\n" + 
				"{\n" + 
				"  \"Carbohydrates\" : [ \"2g\" ],\n" + 
				"  \"Cholesterol\" : [ \"0mg\" ],\n" + 
				"  \"Comments\" : [ \"Yogurt cucumber spread.\" ],\n" + 
				"  \"Country of Origin\" : [ \"Canada\" ],\n" + 
				"  \"Dietary Fibre\" : [ \"0g\" ],\n" + 
				"  \"Fat\" : [ \"2.5g\" ],\n" + 
				"  \"Img\" : [ \"http://www.grocerygateway.com/sites/images/medium/M52503.jpg\" ],\n" + 
				"  \"Ingredients\" : [ \"bacterial culture\", \"canola oil\", \"carrageenan\", \"citric acid\", \"cucumber\", \"diglycerides\", \"flavour\", \"garlic\", \"garlic oil\", \"garlic puree\", \"guar gum\", \"gums\", \"hydrogenated coconut oil\", \"lactic acid\", \"locust bean gum\", \"milk ingredients\", \"mono\", \"olive oil\", \"parsley\", \"potassium sorbate.\", \"purity gum\", \"salt\", \"skim milk powder\", \"sorbic acid\", \"spices\", \"tzatziki sauce\", \"vegetable oil\", \"vinegar\", \"water\", \"yogurt\", \"yogurt powder\" ],\n" + 
				"  \"Price\" : [ \"$3.29\" ],\n" + 
				"  \"Protein\" : [ \"1g\" ],\n" + 
				"  \"Saturates\" : [ \"0.1g\" ],\n" + 
				"  \"Size\" : [ \"227g\" ],\n" + 
				"  \"Sodium\" : [ \"25mg\" ],\n" + 
				"  \"Sugars\" : [ \"0g\" ],\n" + 
				"  \"Title\" : [ \"Longos Dip Tzatziki\" ],\n" + 
				"  \"Trans Fat\" : [ \"0g\" ]\n" + 
				"}\n" + 
				"{\n" + 
				"  \"Img\" : [ \"http://www.grocerygateway.com/sites/images/medium/M52703.jpg\" ],\n" + 
				"  \"Price\" : [ \"$21.82\" ],\n" + 
				"  \"Size\" : [ \"250g-450g\" ],\n" + 
				"  \"Title\" : [ \"VG Boneless Rib Steak\" ]\n" + 
				"}\n" + 
				"{\n" + 
				"  \"Img\" : [ \"http://www.grocerygateway.com/sites/images/medium/M98805.jpg\" ],\n" + 
				"  \"Price\" : [ \"$6.99\" ],\n" + 
				"  \"Size\" : [ \"370g\" ],\n" + 
				"  \"Title\" : [ \"Tricolour Peppers Sliced\" ]\n" + 
				"}\n" + 
				"{\n" + 
				"  \"Carbohydrates\" : [ \"15g\" ],\n" + 
				"  \"Cholesterol\" : [ \"0mg\" ],\n" + 
				"  \"Comments\" : [ \"This unique blend of tomatoes, brown sugar and garlic make for great tasting ribs and chicken every time. Ideal for marinating, cooking, glazing or barbecuing.\" ],\n" + 
				"  \"Country of Origin\" : [ \"Canada\" ],\n" + 
				"  \"Dietary Fibre\" : [ \"0g\" ],\n" + 
				"  \"Fat\" : [ \"0.3g\" ],\n" + 
				"  \"Img\" : [ \"http://www.grocerygateway.com/sites/images/medium/M1380.jpg\" ],\n" + 
				"  \"Ingredients\" : [ \"brown sugar\", \"fructose syrup\", \"garlic powder\", \"salt\", \"spices\", \"sugar\", \"tomato paste\", \"water\", \"white vinegar\", \"worcestershire sauce\", \"xanthan gum.\" ],\n" + 
				"  \"Price\" : [ \"$3.69\" ],\n" + 
				"  \"Protein\" : [ \"0.4g\" ],\n" + 
				"  \"Saturates\" : [ \"0g\" ],\n" + 
				"  \"Size\" : [ \"500mL\" ],\n" + 
				"  \"Sodium\" : [ \"230mg\" ],\n" + 
				"  \"Sugars\" : [ \"14g\" ],\n" + 
				"  \"Title\" : [ \"Diana Sauce Rib & Chicken\" ]\n" + 
				"}\n" + 
				"{\n" + 
				"  \"Comments\" : [ \"Rhubarb adds a zippy signature to pies and tarts. When combined with strawberries, raspberries, apples, and other fruits, the flavor only gets better. Rhubarb also makes a terrific sauce for chicken, venison, halibut and salmon. Adding diced rhubarb to muffins and biscuit recipes makes them sing with flavour.\" ],\n" + 
				"  \"Country of Origin\" : [ \"Canada\" ],\n" + 
				"  \"Img\" : [ \"http://www.grocerygateway.com/sites/images/medium/M79489.jpg\" ],\n" + 
				"  \"Price\" : [ \"$2.64\" ],\n" + 
				"  \"Size\" : [ \"300-400g bunch\" ],\n" + 
				"  \"Title\" : [ \"Fresh Rhubarb\" ]\n" + 
				"}\n" + 
				"{\n" + 
				"  \"Carbohydrates\" : [ \"16g\" ],\n" + 
				"  \"Cholesterol\" : [ \"50mg\" ],\n" + 
				"  \"Comments\" : [ \"Contains milk, soy and wheat. Keep refrigerated.\" ],\n" + 
				"  \"Dietary Fibre\" : [ \"1g\" ],\n" + 
				"  \"Fat\" : [ \"15g\" ],\n" + 
				"  \"Img\" : [ \"http://www.grocerygateway.com/sites/images/medium/M92275.jpg\" ],\n" + 
				"  \"Ingredients\" : [ \"butter\", \"calcium chloride\", \"caramel colour\", \"carrageenan\", \"cellulose gel\", \"cellulose gum\", \"citric acid\", \"cream\", \"crushed tomatoes\", \"dehydrated mushroom\", \"diglycerides\", \"disodium guanylate\", \"disodium inosinate\", \"flavoring\", \"herbs\", \"milk\", \"modified corn starch\", \"mono\", \"mushrooms\", \"onions\", \"polysorbate 80\", \"potassium sorbate.\", \"salt\", \"sherry wine\", \"skim milk powder\", \"sodium citrate\", \"soya\", \"spices\", \"sugar\", \"tomato juice\", \"tomatoes\", \"water\", \"wheat\", \"wheat flour\", \"yeast extract\" ],\n" + 
				"  \"Price\" : [ \"$6.99\" ],\n" + 
				"  \"Protein\" : [ \"3g\" ],\n" + 
				"  \"Saturates\" : [ \"10g\" ],\n" + 
				"  \"Size\" : [ \"700mL\" ],\n" + 
				"  \"Sodium\" : [ \"390mg\" ],\n" + 
				"  \"Sugars\" : [ \"6g\" ],\n" + 
				"  \"Title\" : [ \"Longos Forest Mushroom Soup\" ],\n" + 
				"  \"Trans Fat\" : [ \"0.5g\" ]\n" + 
				"}\n", newWriter.toString());
	}

	private void setupTestFiles() throws IOException {
		File[] listFiles = new File("src/test/groceryGatewayFiles/")
				.listFiles();
		for (File file : listFiles) {
			server.setResource("Product/?n=4294961273&p=" + file.getName(),
					FileUtils.readFileToString(file));
		}
	}

	private void setupLogin() {
		server.setResource("/Login/Login.aspx", new PseudoServlet() {
			@Override
			public WebResource getPostResponse() throws IOException {
				return new WebResource("");
			}
		});
	}

}
