<%-- 
    Document   : uploader
    Created on : 15 juin 2021, 09:08:18
    Author     : Ir Andy
--%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>Envoi de fichier</title>
        <link type="text/css" rel="stylesheet" href="inc/form.css">
    </head>
    <body>
        <form action="upload" method="post" enctype="multipart/form-data">
            <fieldset>
                <legend>Envoi de fichier</legend>
                <label for="description">Description du fichier</label>
                <input type="text" id="description" name="description" value="" />
                <span class="succes"><c:out value="${description}"/></span>
                <br />
                <label for="fichier">Emplacement du fichier <span class="requis">*</span></label>
                <input type="file" id="fichier" name="fichier" />
                <span class="succes"><c:out value="${fichier}"/></span>
                <br />
                <input type="submit" value="Envoyer"class="sansLabel" />
                <br />
            </fieldset>
        </form>
    </body>
</html>