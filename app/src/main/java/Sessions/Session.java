package Sessions;

import ASyncTaskClasses.DB_BackgroundWorker;

public class Session {

    private Boolean isOnline;
    private String name;
    private int id;

    private String erro;


    public Session(){
        erro="";
        isOnline=false;
        name="";
        id = 0;
    }

    public void terminar(){

        isOnline=false;
        name="";
        id = 0;

    }

    public boolean validadeLogin(String pNome, String pPass){

        String vNome,vPass;
        vNome=pNome;
        vPass=pPass;

        if(!vNome.matches("^[a-zA-Z0-9_]+?$")){

            erro = "O nome só pode ter letras, numeros ou _";

            return false;
        }

        if(vNome.trim().equals("")){

            erro = "O nome está vazio";

            return false;
        }

        if(vPass.trim().equals("")){

            erro = "A palavra passe está vazia";

            return false;
        }
        return true;

    }

    public boolean validadeRegister(String pNome, String pEmail, String pPass, String pPassConf){

        String vNome,vEmail,vPass,vPassConf;
        vNome = pNome;
        vEmail = pEmail;
        vPass = pPass;
        vPassConf = pPassConf;


        if(!vNome.matches("^[a-zA-Z0-9_]+?$")){

            erro = "O nome só pode ter letras, numeros ou _";

            return false;
        }

        if(vNome.trim().equals("")){

            erro = "O nome está vazio";

            return false;
        }

        if(vEmail.trim().equals("")){

            erro = "O E-mail está vazio";

            return false;
        }


        if(vPass.trim().equals("")){

            erro = "A palavra passe está vazia";

            return false;
        }

        if(vPassConf.trim().equals("")){

            erro = "A palavra passe está vazia";

            return false;
        }

        if(!vPass.equals(vPassConf)){

            erro = "As palavra passes não são iguais.";
        }

        return true;

    }

    public boolean validadeComment(int pRating, String pComment){

        int vRating;
        String vComment;
        vRating = pRating;
        vComment = pComment;

        if(!vComment.matches("^[\\na-zA-Z0-9_ ]+?$")){

            erro = "O comentário só pode ter letras, numeros ou _";

            return false;
        }

        if(vRating<=0 || vRating>10){

            erro = "A classificação só pode ser um número entre 1-10";

            return false;

        }

        return true;

    }

    public boolean login(String vNome, String vPass){
        boolean isOk = false;


        boolean isValid = validadeLogin(vNome, vPass);

        if(!isValid) {
            return false;
        }

        String type="login";
        DB_BackgroundWorker db_backgroundWorker = new DB_BackgroundWorker();


        db_backgroundWorker.execute(type, vNome, vPass);

        String[] result = db_backgroundWorker.getFinal_resultOneArr();

        boolean gotResult = true;

        try {
            int i=0;
            while(result==null){
                result = db_backgroundWorker.getFinal_resultOneArr();
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

        if(gotResult) {
            int code;
            if (result != null) {
                code = Integer.parseInt(result[0]);
            }else{
                System.out.println("Connection timeout");
                erro = "Erro: Time out";
                return false;
            }
            if(code==5) {

                String aux = result[2].toLowerCase();
                if (vNome.equals(aux)) {
                    isOk = true;
                }

                if (isOk) {
                    isOnline = true;
                    this.name = vNome;
                    this.id = Integer.parseInt(result[1]);
                }else{
                    System.out.println("Dados incorretos");
                    erro = "Erro: Dados incorretos";
                    return false;
                }
            }else{
                System.out.println("Erro: "+code+ " - "+ result[1]);
                erro = "Erro: " + result[1];
                return false;
            }
        }else{
            System.out.println("Connection timeout");
            erro = "Erro: Time out";
            return false;
        }
        return true;

    }

    public Boolean register(String pNome, String pEmail, String pPass, String pPassConf){

        //boolean isOk = false;

        String vNome,vEmail,vPass,vPassConf;
        vNome = pNome;
        vEmail = pEmail;
        vPass = pPass;
        vPassConf = pPassConf;

        boolean isValid = validadeRegister(vNome, vEmail, vPass, vPassConf);

        if(!isValid) {
            return false;
        }

        String type="register";
        DB_BackgroundWorker db_backgroundWorker = new DB_BackgroundWorker();


        db_backgroundWorker.execute(type, vNome, vEmail, vPass, vPassConf);

        String[] result = db_backgroundWorker.getFinal_resultOneArr();

        boolean gotResult = true;

        try {
            int i=0;
            while(result==null){
                result = db_backgroundWorker.getFinal_resultOneArr();
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

        if(gotResult) {
            int code;
            if (result != null) {
                code = Integer.parseInt(result[0]);
            }else{
                System.out.println("Connection timeout");
                erro = "Erro: Time out";
                return false;
            }
            if(code==5) {
                System.out.println("Sucesso!");
            }else{
                System.out.println("Erro: "+code+ " - "+ result[1]);
                erro = "Erro: " + result[1];
                return false;
            }
        }else{
            System.out.println("Connection timeout");
            erro = "Erro: Time out";
            return false;
        }
        return true;
    }

    public Boolean comment(int pRating, String pComment, String pAtivNome){

        //boolean isOk = false;

        int vRating;
        String vComment, vAtivNome;
        vRating = pRating;
        vComment = pComment;
        vAtivNome = pAtivNome;

        boolean isValid = validadeComment(vRating, vComment);

        if(!isValid) {
            return false;
        }

        String type="comment";
        DB_BackgroundWorker db_backgroundWorker = new DB_BackgroundWorker();


        db_backgroundWorker.execute(type, ""+vRating, vComment, vAtivNome, ""+getId());

        String[] result = db_backgroundWorker.getFinal_resultOneArr();

        boolean gotResult = true;

        try {
            int i=0;
            while(result==null){
                result = db_backgroundWorker.getFinal_resultOneArr();
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

        if(gotResult) {
            int code;
            if (result != null) {
                code = Integer.parseInt(result[0]);
            }else{
                System.out.println("Connection timeout");
                erro = "Erro: Time out";
                return false;
            }
            if(code==5) {
                System.out.println("Sucesso!");
            }else{
                System.out.println("Erro: "+code+ " - "+ result[1]);
                erro = "Erro: " + result[1];
                return false;
            }
        }else{
            System.out.println("Connection timeout");
            erro = "Erro: Time out";
            return false;
        }
        return true;
    }
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getErro(){
        return erro;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }
}
