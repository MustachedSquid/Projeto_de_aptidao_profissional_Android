package xyz.inkredible.pap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import ASyncTaskClasses.DB_BackgroundWorker;
import ASyncTaskClasses.DownloadImageTask;
import Atividades.Atividade;
import Sessions.Session;

public class MainActivity extends AppCompatActivity {

    public Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        session = new Session();


        loadStartPage();


        System.out.println("End of onCreate() main function");



    }
    public void loadLoginMenu(View view) {

        setContentView(R.layout.login);
    }

    public void loadRegisterMenu(View view) {

        setContentView(R.layout.register);
    }

    public void terminarSessao(View view) {

        session.terminar();

        System.out.println("Sessão terminada!");


        loadStartPage();


    }


    public void doViewComment(Atividade pAtiv){



        setContentView(R.layout.comments_linear);

        LinearLayout ll = findViewById(R.id.commentFormLine);
        Spinner cRating = findViewById(R.id.rateSpinner);
        TextView cComment = findViewById(R.id.rateEdit);
        Button bComment = findViewById(R.id.rateComment);

        TextView cAtivId = findViewById(R.id.campoNomeAtivCom2);
        cAtivId.setText("" + pAtiv.getId());

        if(!session.getIsOnline()){
            ll.setVisibility(View.INVISIBLE);
            cRating.setVisibility(View.INVISIBLE);
            cComment.setVisibility(View.INVISIBLE);
            bComment.setVisibility(View.INVISIBLE);
        }else{
            ll.setVisibility(View.VISIBLE);
            cRating.setVisibility(View.VISIBLE);
            cComment.setVisibility(View.VISIBLE);
            bComment.setVisibility(View.VISIBLE);

        }
        Button cVoltar = findViewById(R.id.botBack);
        cVoltar.setOnClickListener(v -> loadActivityPage(pAtiv));
        String type = "viewComment";
        //-----
        DB_BackgroundWorker db_backgroundWorker = new DB_BackgroundWorker();

        db_backgroundWorker.execute(type, ""+pAtiv.getId());

        String[][] result = db_backgroundWorker.getFinalResultArr();

        boolean gotResult = true;

        try {
            int i=0;
            while(result==null){
                result = db_backgroundWorker.getFinalResultArr();
                Thread.sleep(50);
                i++;
                if(i==300){
                    gotResult=false;
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            if (!db_backgroundWorker.hasError) {

                if (gotResult) {

                    LinearLayout main_linear = findViewById(R.id.vertComLinear);

                    if (result != null) {
                        for (int i=0; i<result.length;i++){ //String[0][]
                            LinearLayout line = new LinearLayout(this);
                            line.setOrientation(LinearLayout.HORIZONTAL);

                            line.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            line.setPadding(5,5,5,5);

                            main_linear.addView(line);
                            for (int j=0; j<3;j++){ //String[][0]

                                TextView textView = new TextView(this);
                                textView.setText(result[i][j]);
                                double weight = 1;
                                float w = (float) weight;
                                textView.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, w));
                                textView.setPadding(5,5,5,5);
                                textView.setTextColor(Color.BLACK);

                                textView.setMinWidth(1);
                                line.addView(textView);


                            }
                        }
                    }else{
                        System.out.println("Erro: Erro de ligação");
                        AlertDialog.Builder ad = new AlertDialog.Builder(this);
                        ad.setTitle("Ver comentarios");
                        ad.setMessage("Erro de ligação");
                        ad.create().show();

                    }


                    //show results n stuff
                } else {
                    System.out.println("Erro: Erro de ligação");
                    AlertDialog.Builder ad = new AlertDialog.Builder(this);
                    ad.setTitle("Ver comentarios");
                    ad.setMessage("Erro de ligação");
                    ad.create().show();
                }
            } else {
                System.out.println("" + db_backgroundWorker.erro);
                AlertDialog.Builder ad = new AlertDialog.Builder(this);
                ad.setTitle("Ver comentarios");
                ad.setMessage("" + db_backgroundWorker.erro);
                ad.create().show();
            }
        }catch(ArrayIndexOutOfBoundsException e){
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle("Ver comentarios");
            ad.setMessage("Não foram encontrados resultados");
            ad.create().show();
            loadActivityPage(pAtiv);
        }


    }

    public void doComment(View view){

        /* Activity Page . xml form code
        TextView cRating = findViewById(R.id.campoRating);
        TextView cComment = findViewById(R.id.campoComentario);
        TextView cAtivNome = findViewById(R.id.campoNomeAtivCom);
        */
        Spinner cRating = findViewById(R.id.rateSpinner);
        TextView cComment = findViewById(R.id.rateEdit);
        TextView cAtivNome = findViewById(R.id.campoNomeAtivCom2);

        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Comentar");

        try {
            int rating = Integer.parseInt(cRating.getSelectedItem().toString());
            String comment = cComment.getText().toString();
            String ativNome = cAtivNome.getText().toString();


            boolean isEmpty = false;
            if (rating <= 0 || rating > 10 || comment.trim().equals("")) {
                isEmpty = true;
            }


            if (!isEmpty) {
                boolean success = session.comment(rating, comment, ativNome);


                if (success) {
                    System.out.println("Comentar: OK");
                    ad.setMessage("Comentario adicionado com sucesso");
                    ad.create().show();
                    loadStartPage();
                } else {
                    System.out.println("Comentar: ERRO");
                    ad.setMessage("" + session.getErro());
                    ad.create().show();
                }
            } else {
                System.out.println("Comentar: ERRO");
                ad.setMessage("Dados inválidos");
                ad.create().show();
            }
        }catch(NumberFormatException e){
            System.out.println("Comentar: ERRO");
            ad.setMessage("Dados inválidos");
            ad.create().show();
        }

    }

    public void doLogin(View view){
        EditText campoNome = findViewById(R.id.campoNome);
        String nome = campoNome.getText().toString();

        EditText campoPassword = findViewById(R.id.campoPassword);
        String password = campoPassword.getText().toString();

        boolean success = session.login(nome,password);

        System.out.println(session.getId());
        System.out.println(session.getName());
        System.out.println(session.getIsOnline());

        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Login");




        if(success) {
            System.out.println("Login: OK");
            ad.setMessage("Login com sucesso");
            ad.create().show();
            loadStartPage();
        }else{
            System.out.println("Login: ERRO");
            ad.setMessage(""+ session.getErro());
            ad.create().show();
        }
    }

    public void doRegister(View view){

        EditText campoNome = findViewById(R.id.campoRNome);
        String nome = campoNome.getText().toString();

        EditText campoEmail = findViewById(R.id.campoREmail);
        String email = campoEmail.getText().toString();

        EditText campoPassword = findViewById(R.id.campoRPass);
        String password = campoPassword.getText().toString();

        EditText campoPasswordConf = findViewById(R.id.campoRPassConf);
        String passwordConf = campoPasswordConf.getText().toString();

        Boolean success = session.register(nome,email,password,passwordConf);

        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Registar");

        if(success) {
            System.out.println("Registo: OK");
            ad.setMessage("Registo com sucesso");
            ad.create().show();
            loadStartPage();
        }else{
            System.out.println("Registo: ERRO");
            ad.setMessage(""+ session.getErro());
            ad.create().show();
        }

    }

    public void doSearch(View view) {


        EditText campoPesquisa = findViewById(R.id.campoPesquisa);
        String search = campoPesquisa.getText().toString();

        String local = "none";
        String type = "search";

        //-----
        DB_BackgroundWorker db_backgroundWorker = new DB_BackgroundWorker();

        db_backgroundWorker.execute(type, search, local);

        String[][] result = db_backgroundWorker.getFinalResultArr();

        boolean gotResult = true;

        try {
            int i=0;
            while(result==null){
                result = db_backgroundWorker.getFinalResultArr();
                Thread.sleep(50);
                i++;
                if(i==300){
                    gotResult=false;
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            if (!db_backgroundWorker.hasError) {

                if (gotResult) {
                    if (result != null) {
                        setResultLinear(result);
                    }else{

                        System.out.println("Erro: Erro de ligação");
                        AlertDialog.Builder ad = new AlertDialog.Builder(this);
                        ad.setTitle("Pesquisa");
                        ad.setMessage("Erro: Erro de ligação");
                        ad.create().show();
                    }
                } else {
                    System.out.println("Erro: Conexão falhou");
                }
            } else {
                System.out.println("" + db_backgroundWorker.erro);
                AlertDialog.Builder ad = new AlertDialog.Builder(this);
                ad.setTitle("Pesquisa");
                ad.setMessage("" + db_backgroundWorker.erro);
                ad.create().show();
            }
        }catch(ArrayIndexOutOfBoundsException e){
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle("Pesquisa");
            ad.setMessage("Não foram encontrados resultados");
            ad.create().show();
            loadStartPage();
        }



    }

    @Override
    public void onBackPressed() {

            loadStartPage();

    }

    public void setResultLinear(String[][] result){


        setContentView(R.layout.search_linear);

        setTitle("Resultados");


        LinearLayout main_linear = findViewById(R.id.vertLinear);


        for (int i=0; i<result.length;i++){ //String[0][]
            LinearLayout line = new LinearLayout(this);
            line.setOrientation(LinearLayout.HORIZONTAL);

            line.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            line.setPadding(5,5,5,5);

            main_linear.addView(line);
            for (int j=1; j<=4;j++){ //String[][0]

                if(j==1) {
                    final int i_final = i;

                    Button botNomeAtiv = new Button(this);
                    botNomeAtiv.setText(result[i_final][j]);
                    botNomeAtiv.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                    botNomeAtiv.setPadding(5,5,5,5);

                    botNomeAtiv.setOnClickListener(v -> {

                        Atividade atividade = new Atividade(Integer.parseInt(result[i_final][0]),result[i_final][1],result[i_final][3],result[i_final][4],result[i_final][5],result[i_final][6]);

                        loadActivityPage(atividade);
                    });

                    botNomeAtiv.setTextColor(Color.BLACK);
                    botNomeAtiv.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow_light));

                    line.addView(botNomeAtiv);
                }else{
                    TextView textView = new TextView(this);
                    textView.setText(result[i][j]);
                    textView.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

                    textView.setPadding(5,5,5,5);
                    textView.setTextColor(Color.BLACK);

                    textView.setMinWidth(1);
                    line.addView(textView);


                }
            }
        }
    }

    public void loadActivityPage(Atividade vAtiv){


        setContentView(R.layout.activity_page);

        setTitle(""+vAtiv.getNome());

        TextView cAtivNome = findViewById(R.id.campoNomeAtivCom);
        cAtivNome.setText(vAtiv.getNome());

        ImageView img0 = findViewById(R.id.ativProfileImage);

        String img_url = ""+vAtiv.getImg();

        System.out.println("url:  " + img_url);

        DownloadImageTask downloadImageTask = new DownloadImageTask(img0);

        downloadImageTask.execute(img_url);

        TextView campoLocal = findViewById(R.id.loc);

        campoLocal.setText(vAtiv.getLocal());

        TextView campoDesc = findViewById(R.id.desc);

        campoDesc.setMovementMethod(new ScrollingMovementMethod());
        campoDesc.setText(vAtiv.getDescricao());

        Button cLinkMaps = findViewById(R.id.linkMapsButton);
        Button cViewComments = findViewById(R.id.botVerComentarios);

        cViewComments.setOnClickListener(v -> doViewComment(vAtiv));

        try {
            final String maps_url = ""+vAtiv.getLinkMaps();

            System.out.println(""+maps_url);

            //String acentos = "áàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ";

            String regexLinkMaps1 = "^https://www.google.pt/maps/place/[A-Za-z0-9áàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ%+.@!:/,-=]+?$";
            String regexLinkMaps2 = "^https://goo.gl/maps/[A-Za-z0-9áàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ%+.@!:/,-=]+?$";


            if(!maps_url.matches(regexLinkMaps1) && !maps_url.matches(regexLinkMaps2)){

                cLinkMaps.setVisibility(View.GONE);

            }


            cLinkMaps.setOnClickListener(v -> {


                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(maps_url));
                startActivity(intent);
            });



        } catch(ArrayIndexOutOfBoundsException aofbe){
            cLinkMaps.setVisibility(View.GONE);
        }

        TextView cRating = findViewById(R.id.campoRating);
        TextView cComment = findViewById(R.id.campoComentario);
        Button bComment = findViewById(R.id.botComentar);

        /*if(!session.getIsOnline()){
            cRating.setVisibility(View.INVISIBLE);
            cComment.setVisibility(View.INVISIBLE);
            bComment.setVisibility(View.INVISIBLE);
        }else{
            cRating.setVisibility(View.VISIBLE);
            cComment.setVisibility(View.VISIBLE);
            bComment.setVisibility(View.VISIBLE);

        }*/
        cRating.setVisibility(View.INVISIBLE);
        cComment.setVisibility(View.INVISIBLE);
        bComment.setVisibility(View.INVISIBLE);

    }

    private void loadStartPage(){
        setContentView(R.layout.activity_main);
        setTitle("Procurar Atividades Portuguesas");


        Button login = findViewById(R.id.botLogMain);
        Button sair = findViewById(R.id.botSairMain);
        Button registar = findViewById(R.id.botRegMain);
        TextView campoName = findViewById(R.id.campoUserName);

        if(session.getIsOnline()){
            login.setVisibility(View.INVISIBLE);
            registar.setVisibility(View.INVISIBLE);
            campoName.setText(session.getName());
        }else{
            sair.setVisibility(View.INVISIBLE);
            campoName.setVisibility(View.INVISIBLE);
        }
    }

    public void onAdd(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://pap.inkredible.xyz/"));
        startActivity(intent);
    }


}
