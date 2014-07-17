var urlIterator = new UrlIterator(function(pIndex){
	var prod=[100505,1274,68494,52503,100519,98805,1380,79489,92275];
    if (pIndex < prod.length) {
    	return "http://localhost:####/Product/?n=4294961273&p=" + (prod[pIndex]);
	 } else {
		return null;
	 }
});
urlIterator.forEach(function(pContext) {
    processDetailPage(pContext);
    pContext.flush();
});


function processDetailPage(pContext){
	if (!pContext.getJq("#ctl00_c_ProdImage").isEmpty()){
		pContext.emit("Img", pContext.getJq("#ctl00_c_ProdImage").get(0).attr("src"));
		pContext.emit("Size", pContext.getJqText(".MasterTable_Prd tr td:nth-child(3)"));
		pContext.emit("Price", pContext.getJqText(".MasterTable_Prd tr td:nth-child(4) span:nth-child(1)"));
		pContext.emit("Title", pContext.getJqText("#ctl00_c_ProdName"));
		pContext.emit("Fat", pContext.getJqText("#ctl00_c_Nutrs_ctl00_Value"));
		pContext.emit("Polyunsaturates", pContext.getJqText("#ctl00_c_Nutrs_ctl01_Value"));
		pContext.emit("Omega-6 Poly", pContext.getJqText("#ctl00_c_Nutrs_ctl02_Value"));
		pContext.emit("Omega-3 Poly", pContext.getJqText("#ctl00_c_Nutrs_ctl03_Value"));
		pContext.emit("Monounsaturates", pContext.getJqText("#ctl00_c_Nutrs_ctl04_Value"));
		pContext.emit("Saturates", pContext.getJqText("#ctl00_c_Nutrs_ctl05_Value"));
		pContext.emit("Trans Fat", pContext.getJqText("#ctl00_c_Nutrs_ctl06_Value"));
		pContext.emit("Cholesterol", pContext.getJqText("#ctl00_c_Nutrs_ctl07_Value"));
		pContext.emit("Sodium", pContext.getJqText("#ctl00_c_Nutrs_ctl08_Value"));
		pContext.emit("Potassium", pContext.getJqText("#ctl00_c_Nutrs_ctl09_Value"));
		pContext.emit("Carbohydrates", pContext.getJqText("#ctl00_c_Nutrs_ctl10_Value"));
		pContext.emit("Dietary Fibre", pContext.getJqText("#ctl00_c_Nutrs_ctl11_Value"));
		pContext.emit("Sugars", pContext.getJqText("#ctl00_c_Nutrs_ctl12_Value"));
		pContext.emit("Starch", pContext.getJqText("#ctl00_c_Nutrs_ctl13_Value"));
		pContext.emit("Protein", pContext.getJqText("#ctl00_c_Nutrs_ctl14_Value"));
		pContext.emit("Other Nutrients", pContext.getJqText("#ctl00_c_Nutrs_ctl15_Value"));
		pContext.emit("Calcium", pContext.getJqText("#ctl00_c_Nutrs_ctl16_Value"));
		pContext.emit("Iron", pContext.getJqText("#ctl00_c_Nutrs_ctl17_Value"));
		pContext.emit("Lycopene", pContext.getJqText("#ctl00_c_Nutrs_ctl18_Value"));
		pContext.emit("Vitamin A", pContext.getJqText("#ctl00_c_Nutrs_ctl19_Value"));
		pContext.emit("Thiamine B1", pContext.getJqText("#ctl00_c_Nutrs_ctl20_Value"));
		pContext.emit("Riboflavin B2", pContext.getJqText("#ctl00_c_Nutrs_ctl21_Value"));
		pContext.emit("Niacin B3", pContext.getJqText("#ctl00_c_Nutrs_ctl22_Value"));
		pContext.emit("Pyridoxine B6", pContext.getJqText("#ctl00_c_Nutrs_ctl23_Value"));
		pContext.emit("Folic Acid B9", pContext.getJqText("#ctl00_c_Nutrs_ctl24_Value"));
		pContext.emit("Vitamin B12", pContext.getJqText("#ctl00_c_Nutrs_ctl25_Value"));
		pContext.emit("Pantothenic Acid B", pContext.getJqText("#ctl00_c_Nutrs_ctl26_Value"));
		pContext.emit("Biotin B", pContext.getJqText("#ctl00_c_Nutrs_ctl27_Value"));
		pContext.emit("Vitamin C", pContext.getJqText("#ctl00_c_Nutrs_ctl28_Value"));
		pContext.emit("Vitamin D", pContext.getJqText("#ctl00_c_Nutrs_ctl29_Value"));
		pContext.emit("Vitamin E", pContext.getJqText("#ctl00_c_Nutrs_ctl30_Value"));
		pContext.emit("Phosphorus", pContext.getJqText("#ctl00_c_Nutrs_ctl31_Value"));
		pContext.emit("Magnesium", pContext.getJqText("#ctl00_c_Nutrs_ctl32_Value"));
		pContext.emit("Zinc", pContext.getJqText("#ctl00_c_Nutrs_ctl33_Value"));

		getWeirdText("Country of Origin", 3, "Country of Origin", pContext);
		getWeirdText("Ingredients", 5, "Ingredients", pContext, true);
		getWeirdText("Comments", 7, "Comments", pContext);
	}
}
function getWeirdText(pTextToMatch, pIndex, pEmitName, pContext, pLower){
	var h3s = pContext.getDocument().getElementsMatchingText(pTextToMatch);
	if (h3s != null && !h3s.isEmpty()){
		var value = h3s.get(h3s.size()-1).siblingNodes().get(pIndex).toString().trim();
		value = pLower ? value.toLowerCase() : value;
		value = pLower ? value.split("\\s*&amp;\\s*|\\s*,\\s*|\\s*and\\s*|\\s*contain\\s*|\\s*\\(\\s*|\\s*\\)\\s*") : value;
		pContext.emit(pEmitName, value);			
	}
}
