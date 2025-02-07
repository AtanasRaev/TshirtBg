package bg.tshirt.database.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String path;

    @Column(unique = true, nullable = false)
    private String publicId;

    @ManyToOne
    @JoinColumn(name = "cloth_id", referencedColumnName = "id")
    private Clothing cloth;

    public Image() {
    }

    public Image(String path, String publicId, Clothing cloth) {
        this.path = path;
        this.publicId = publicId;
        this.cloth = cloth;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public Clothing getCloth() {
        return cloth;
    }

    public void setCloth(Clothing cloth) {
        this.cloth = cloth;
    }
}
