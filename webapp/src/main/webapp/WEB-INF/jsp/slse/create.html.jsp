
<html>
	<body>
		<h1>Service-Level Semantic Entity Constructor</h1>

		<form method="GET" action="createslse/">

            Name of the new rule:
            <input type="text" name="name" value="rule1" /> <br />

			<a href="http://www.w3.org/TR/rdf-sparql-query/">SPARQL</a>
			query for element entity selection (use "?element" and "?slse" as variable names): <br />
			<textarea name="elementsQuery" cols="100" rows="20">
PREFIX dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>
PREFIX : <http://spitfire.ibr.cs.tu-bs.de:8080/static/ontology.owl#>

SELECT ?element ?entity WHERE {
    ?element ssn:attachedSystem ?sensor .
    ?sensor a ssn:Sensor .
    ?sensor ssn:featureOfInterest ?entity .
}
			</textarea><br />

			<input type="submit" />
		</form>
	</body>
</html>



