import * as functions from 'firebase-functions';

// Start writing Firebase Functions
// https://firebase.google.com/docs/functions/typescript

functions.https.onRequest((request, response) => {
 response.send("<!DOCTYPE html>\n" +
     "<html lang=\"en\">\n" +
     "<head>\n" +
     "    <meta charset=\"UTF-8\">\n" +
     "    <title>Title</title>\n" +
     "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css\">\n" +
     "    <link rel=\"stylesheet\" href=\"https://www.w3schools.com/w3css/4/w3.css\">\n" +
     "</head>\n" +
     "<body>\n" +
     "\n" +
     "<div class=\"w3-top\">\n" +
     "Du benötigst die APP: Groupexpenses\n" +
     "</div>\n" +
     "</body>\n" +
     "</html>");
});
