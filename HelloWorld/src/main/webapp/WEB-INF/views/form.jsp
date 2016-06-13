<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title>Form Test</title>
</head>
<body>

<h2>Enter Information</h2>
<form:form method="POST" action="/interns/addForm">
   <table>
   <tr>
        <td><form:label path="id">ID Number</form:label></td>
        <td><form:input path="id" /></td>
    </tr>
    <tr>
        <td><form:label path="message">Message</form:label></td>
        <td><form:input path="message" /></td>
    </tr>
    <tr>
        <td><form:label path="sender">Your Name</form:label></td>
        <td><form:input path="sender" /></td>
    </tr>
    <tr>
        <td><form:label path="recipient">Recipient</form:label></td>
        <td><form:input path="recipient" /></td>
    </tr>
    <tr>
        <td colspan="2">
            <input type="submit" value="Submit"/>
        </td>
    </tr>
</table>  
</form:form>
</body>
</html>