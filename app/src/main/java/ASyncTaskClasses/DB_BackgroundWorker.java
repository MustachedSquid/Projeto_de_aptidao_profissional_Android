package ASyncTaskClasses;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class DB_BackgroundWorker extends AsyncTask<String,Void,String> {

    //Link para o servidor com a base de dados e os ficheiros de API para o android.
    //private final String link_servidor = "https://pap.inkredible.xyz/";


    private final String link_servidor = "https://pap.inkredible.xyz/";
    //resultados
    private String[] final_resultOneArr;
    private String[][] final_resultArr;


    public String erro;
    public Boolean hasError;

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];


        erro = "";
        hasError = false;

        boolean isValid=true;
        String regex = "^[A-Za-z0-9áàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ%+.@!:/,=_ ]+?$";
        for (int i = 0; i < params.length; i++){
            String aux = params[i];
            if(!aux.trim().equals("")) {
                if (!aux.matches(regex)) {
                    isValid = false;
                    hasError = true;
                    erro = "Dados inválidos";
                }
            }


        }

        if(isValid) {
            switch (type) {
                case "search":
                    pesquisa(params);
                    break;

                case "login":
                    login(params);
                    break;

                case "register":
                    registar(params);
                    break;
                case "comment":
                    comment(params);
                    break;
                case "viewComment":
                    viewComment(params);
                    break;
            }
        }else{
            System.out.println("ERRO: Dados inválidos!");
            erro = "Erro: "+ erro;
            hasError = true;
        }


        return null;
    }


    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(String result) {

    }

    private void pesquisa(String... params){

        String search_url = link_servidor+"android/search.php";
        String search = params[1];
        String local = params[2];


        try {
            URL url = new URL(search_url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String post_data = "campoPesquisa" + "=" + search + "&" + "campoLocal" + "=" + local;

            System.out.println("POST: " + post_data);
            bufferedWriter.write(post_data);
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String result = "";
            String line;


            while ((line = bufferedReader.readLine()) != null) {

                result += line;

            }

            System.out.println("RESULT: "+ result);

            try {
                String[] lineArr = result.split("#");

                String[][] resultArr = new String[lineArr.length][6];

                for (int i = 0; i < lineArr.length; i++) {

                    resultArr[i] = lineArr[i].split(";");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


                final_resultArr = resultArr;

            }catch (NullPointerException e){
                System.out.println("Resultados não encontrados");
                hasError = false;
                erro = "";
            }



        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void viewComment(String... params){

        String search_url = link_servidor+"android/viewComments.php";
        String search = params[1];


        try {
            URL url = new URL(search_url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String post_data = URLEncoder.encode("campoPesquisa", "UTF-8") + "=" + URLEncoder.encode(search, "UTF-8");

            bufferedWriter.write(post_data);
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String result = "";
            String line;


            while ((line = bufferedReader.readLine()) != null) {

                result += line;

            }


            try {
                String[] lineArr = result.split("#");

                String[][] resultArr = new String[lineArr.length][3];

                for (int i = 0; i < lineArr.length; i++) {

                    resultArr[i] = lineArr[i].split(";");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


                final_resultArr = resultArr;

            }catch (NullPointerException e){
                System.out.println("Resultados não encontrados");
                hasError = false;
                erro = "";
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login(String... params){
        String login_url = link_servidor+"android/login.php";
        String nome = params[1];
        String password = params[2];


        try {
            URL url = new URL(login_url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String post_data = URLEncoder.encode("campoNome", "UTF-8") + "=" + URLEncoder.encode(nome, "UTF-8") + "&" +
                    URLEncoder.encode("campoPassword", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

            bufferedWriter.write(post_data);
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String result = "";
            String line;


            while ((line = bufferedReader.readLine()) != null) {

                result += line;

            }

            boolean isError = false;
            for(int i=0;i<4;i++){
                if (result.equals("" + i)) {
                    isError = true;
                    break;
                }
            }
            System.out.println(result);
            if(!isError){

                System.out.println("DBWorker: Login OK");
                String[] auxArr = result.split(";");

                for (int i = 0; i < auxArr.length;i++){
                    System.out.println(auxArr[i]);
                }

                final_resultOneArr = auxArr;
            }else{

                System.out.println("DBWorker: Login BAD");

                final_resultOneArr = result.split(";");

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registar(String... params){
        String login_url = link_servidor+"android/register.php";
        String nome = params[1];
        String email = params[2];
        String password = params[3];
        String passwordConf = params[4];


        try {
            URL url = new URL(login_url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String post_data = URLEncoder.encode("campoNome", "UTF-8") + "=" + URLEncoder.encode(nome, "UTF-8") + "&" +
                    URLEncoder.encode("campoEmail", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")+ "&" +
                    URLEncoder.encode("campoPassword", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")+ "&" +
                    URLEncoder.encode("campoPasswordConf", "UTF-8") + "=" + URLEncoder.encode(passwordConf, "UTF-8");

            bufferedWriter.write(post_data);
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String result = "";
            String line;


            while ((line = bufferedReader.readLine()) != null) {

                result += line;

            }

            boolean isError = false;
            for(int i=0;i<4;i++){
                if (result.equals("" + i)) {
                    isError = true;
                    break;
                }
            }
            System.out.println(result);
            if(!isError){

                System.out.println("DBWorker: Register OK");
                String[] auxArr = result.split(";");

                for (int i = 0; i < auxArr.length;i++){
                    System.out.println(auxArr[i]);
                }

                final_resultOneArr = auxArr;
            }else{

                System.out.println("DBWorker: Register BAD");
                final_resultOneArr = result.split(";");

            }


        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void comment(String... params){
        String comment_url = link_servidor+"android/comment.php";
        String rating = params[1];
        String comment = params[2];
        String ativNome = params[3];
        String userId = params[4];


        try {
            URL url = new URL(comment_url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String post_data = URLEncoder.encode("campoRate", "UTF-8") + "=" + URLEncoder.encode(rating, "UTF-8") + "&" +
                    URLEncoder.encode("campoComentario", "UTF-8") + "=" + URLEncoder.encode(comment, "UTF-8") + "&" +
                    URLEncoder.encode("campoIdAtiv", "UTF-8") + "=" + URLEncoder.encode(ativNome, "UTF-8") + "&" +
                    URLEncoder.encode("campoId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");

            bufferedWriter.write(post_data);
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String result = "";
            String line;


            while ((line = bufferedReader.readLine()) != null) {

                result += line;

            }

            boolean isError = false;
            for(int i=0;i<4;i++){
                if (result.equals("" + i)) {
                    isError = true;
                    break;
                }
            }
            System.out.println(result);
            if(!isError){

                System.out.println("DBWorker: Register OK");
                String[] auxArr = result.split(";");

                for (int i = 0; i < auxArr.length;i++){
                    System.out.println(auxArr[i]);
                }

                final_resultOneArr = auxArr;
            }else{

                System.out.println("DBWorker: Register BAD");

                final_resultOneArr = result.split(";");

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


    public String[][] getFinalResultArr(){


        return final_resultArr;
    }

    public String[] getFinal_resultOneArr() {
        return final_resultOneArr;
    }







    //-------- Special function I use for debugging not rly important yeh
    /*public String resultArrToString(){
        String s="";

        for (int i=0; i<final_resultArr.length;i++){
            for (int j=0; j<final_resultArr[i].length;j++){

                s += final_resultArr[i][j];

                if((j)!=final_resultArr[i].length-1){
                    s+= ";";
                }
            }
            s += ":\n\n";
        }

        return s;
    }
*/


}
