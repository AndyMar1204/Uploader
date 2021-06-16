package com.andy.uploader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 *
 * @author Ir Andy
 */
@MultipartConfig(
        location = "D:\\file_temp",
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class Upload extends HttpServlet {

    public static final String VUE = "/WEB-INF/uploader.jsp";
    public static final String CHAMP_DESCRIPTION = "description";
    public static final String CHAMP_FICHIER = "fichier";
    public static final String CHEMIN = "chemin";
    public static final int TAILLE_TAMPON = 10240; // 10 ko

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        getServletContext().getRequestDispatcher(VUE).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
* Lecture du paramètre 'chemin' passé à la servlet via la
déclaration
* dans le web.xml
         */
        String chemin = this.getServletConfig().getInitParameter(CHEMIN);/* Récupération du contenu du champ de description */
        String description = req.getParameter(CHAMP_DESCRIPTION);
        req.setAttribute(CHAMP_DESCRIPTION, description);
        /*
* Les données reçues sont multipart, on doit donc utiliser la
méthode
* getPart() pour traiter le champ d'envoi de fichiers.
         */
        Part part = req.getPart(CHAMP_FICHIER);/*
* Il faut déterminer s'il s'agit d'un champ classique
* ou d'un champ de type fichier : on délègue cette opération
* à la méthode utilitaire getNomFichier().
         */
        String nomFichier = getNomFichier(part);
        /*
* Si la méthode a renvoyé quelque chose, il s'agit donc d'un champ
* de type fichier (input type="file").
         */
        if (nomFichier != null && !nomFichier.isEmpty()) {
            String nomChamp = part.getName();
            /*
* Antibug pour Internet Explorer, qui transmet pour une raison
* mystique le chemin du fichier local à la machine du client...
*
* Ex : C:/dossier/sous-dossier/fichier.ext
*
* On doit donc faire en sorte de ne sélectionner que le nom et
* l'extension du fichier, et de se débarrasser du superflu.
             */
            nomFichier = nomFichier.substring(nomFichier.lastIndexOf(
                    '/') + 1)
                    .substring(nomFichier.lastIndexOf('\\') + 1);
            /* Écriture du fichier sur le disque */
            ecrireFichier(part, nomFichier, chemin);
            req.setAttribute(nomChamp, nomFichier);
        }
        this.getServletContext().getRequestDispatcher(VUE).forward(
                req, resp);
    }

    /*
* Méthode utilitaire qui a pour unique but d'analyser l'en-tête
"content-disposition",
* et de vérifier si le paramètre "filename" y est présent. Si oui,
alors le champ traité
* est de type File et la méthode retourne son nom, sinon il s'agit
d'un champ de formulaire
* classique et la méthode retourne null.
     */
    private static String getNomFichier(Part part) {
        /* Boucle sur chacun des paramètres de l'en-tête "contentdisposition".
         */
        for (String contentDisposition : part.getHeader("content-disposition").split(";")) {
            /* Recherche de l'éventuelle présence du paramètre "filename".
             */
            if (contentDisposition.trim().startsWith("filename")) {
                /* Si "filename" est présent, alors renvoi de sa valeur,
c'est-à-dire du nom de fichier. */
                return contentDisposition.substring(contentDisposition.indexOf('=') + 1);
            }
        }
        /* Et pour terminer, si rien n'a été trouvé... */
        return null;
    }

    /*
* Méthode utilitaire qui a pour unique but de lire l'InputStream
contenu
* dans l'objet part, et de le convertir en une banale chaîne de
caractères.
     */
    private String getValeur(Part part) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream(), "UTF-8"));
        StringBuilder valeur = new StringBuilder();
        char[] buffer = new char[1024];
        int longueur = 0;
        while ((longueur = reader.read(buffer)) > 0) {
            valeur.append(buffer, 0, longueur);
        }
        return valeur.toString();
    }

    /*
* Méthode utilitaire qui a pour but d'écrire le fichier passé en
paramètre
* sur le disque, dans le répertoire donné et avec le nom donné.
     */
    private void ecrireFichier(Part part, String nomFichier, String chemin) throws IOException {
        /* Prépare les flux. */
        BufferedInputStream entree = null;
        BufferedOutputStream sortie = null;
        try {
            /* Ouvre les flux. */
            entree = new BufferedInputStream(part.getInputStream(),
                    TAILLE_TAMPON);
            sortie = new BufferedOutputStream(new FileOutputStream(new File(chemin + nomFichier)),
                    TAILLE_TAMPON);
            /* ... */
        } finally {
            try {
                sortie.close();
            } catch (IOException ignore) {
            }
            try {
                entree.close();
            } catch (IOException ignore) {
            }
        }
    }
}
