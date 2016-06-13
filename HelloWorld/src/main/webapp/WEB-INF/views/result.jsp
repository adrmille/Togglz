<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title>Your Message</title>
</head>
<body>

<h2>Details</h2>
   <table>
   <tr>
   		<td>ID Number</td>
   		<td>${id}</td>
    <tr>
        <td>Message</td>
        <td>${message}</td>
    </tr>
    <tr>
        <td>Sender</td>
        <td>${sender}</td>
    </tr>
    <tr>
        <td>Recipient</td>
        <td>${recipient}</td>
    </tr>
</table>  
</body>
</html>