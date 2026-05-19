package may_chu;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Lop quan ly ket noi MongoDB - Singleton Pattern
 * Connection string lay tu bien moi truong MONGODB_URI
 * Mac dinh fallback: mongodb://emr:123456@localhost:27020/?authSource=admin
 */
public class MongoKetNoi {

    private static final String TEN_DATABASE = "thuvien_db";
    private static final String TEN_COLLECTION_TAILIEU = "tai_lieu";
    private static final String TEN_COLLECTION_DANHMUC  = "danh_muc";
    private static final String TEN_COLLECTION_TAG      = "tag";

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    // Khoi tao ket noi khi Server bat dau
    public static void khoiDong() {
        try {
            String uri = System.getenv("MONGODB_URI");
            if (uri == null || uri.trim().isEmpty()) {
                uri = "mongodb://emr:123456@localhost:27020/?authSource=admin";
            }

            // Them timeout ngan de khong block server neu MongoDB chua san sang
            // connectTimeoutMS=5000, serverSelectionTimeoutMS=5000
            if (!uri.contains("connectTimeoutMS")) {
                uri += (uri.contains("?") ? "&" : "?")
                     + "connectTimeoutMS=5000&serverSelectionTimeoutMS=5000&socketTimeoutMS=10000";
            }

            mongoClient = MongoClients.create(uri);
            database    = mongoClient.getDatabase(TEN_DATABASE);

            // Tao index de tim kiem ten file nhanh hon
            database.getCollection(TEN_COLLECTION_TAILIEU)
                    .createIndex(new Document("ten_file", "text"));

            System.out.println("[MongoDB] Ket noi thanh cong -> database: " + TEN_DATABASE);
        } catch (Exception loi) {
            System.out.println("[MongoDB] LOI ket noi: " + loi.getMessage());
            System.out.println("[MongoDB] Server van chay nhung KHONG luu duoc vao database!");
        }
    }

    // Dong ket noi khi Server tat
    public static void dongKetNoi() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("[MongoDB] Da dong ket noi.");
        }
    }

    // Tra ve collection tai lieu
    public static MongoCollection<Document> layTaiLieu() {
        return database.getCollection(TEN_COLLECTION_TAILIEU);
    }

    // Tra ve collection danh muc
    public static MongoCollection<Document> layDanhMuc() {
        return database.getCollection(TEN_COLLECTION_DANHMUC);
    }

    // Tra ve collection tag
    public static MongoCollection<Document> layTag() {
        return database.getCollection(TEN_COLLECTION_TAG);
    }
}
