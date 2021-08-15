package Atividades;

public class Atividade {

    private final String nome;
    private final String local;
    private final String descricao;
    private final String linkMaps;
    private final String img;
    private final int id;

    //Constructor
    public Atividade(int id, String nome, String local, String descricao, String img, String linkMaps) {
        this.nome = nome;
        this.local = local;
        this.descricao = descricao;
        this.linkMaps = linkMaps;
        this.img = img;
        this.id = id;
    }

    //Methods




    //Getters and Setter
    public String getNome() {
        return nome;
    }

    public String getLocal() {
        return local;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getLinkMaps() {
        return linkMaps;
    }

    public String getImg() {
        return img;
    }

    public int getId() {
        return id;
    }
}
