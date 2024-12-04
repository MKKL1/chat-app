package Data.Databases;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Data.Models.Channel;
import Data.Models.Community;
import Data.Models.Message;
import Data.Models.Role;
import Data.Models.User;
import DataAccess.DAO.ChannelDAO;
import DataAccess.DAO.CommunityDAO;
import DataAccess.DAO.MessageDAO;
import DataAccess.DAO.RoleDAO;
import DataAccess.DAO.UserDAO;

@androidx.room.Database(entities = {
        Community.class,
        Channel.class,
        Role.class,
        User.class,
        Message.class
}, version = 3, exportSchema = false)
//@TypeConverters({TypeConverters.class})
public abstract class CommunityDB extends RoomDatabase {
    public abstract CommunityDAO communityDAO();
    public abstract ChannelDAO channelDAO();
    public abstract RoleDAO roleDAO();
    public abstract MessageDAO messageDAO();
    public abstract UserDAO userDAO();
    /**
     * Singelton
     */
    private static volatile CommunityDB INSTANCE;

    public static CommunityDB getDataBase(final Context context){
        if (INSTANCE == null){
            synchronized (CommunityDB.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), CommunityDB.class, "ChatAppDB")
//                        migracja bazy danych
                        .addCallback(roomDatabaseCallback)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return INSTANCE;
    }

    /**
     * usługa wykonawcza do wykonywania zadań w osobnym wątku
     */
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * obiekt obsługujący callback'i zwiazane ze zdarzeniami bazy danych np onCreate, onOpen
     */
    private static RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        //        pierwsze uruchomienie gdy baza nie istnieje = stworzenie bazy danych
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                CommunityDAO communityDAO = INSTANCE.communityDAO();
                ChannelDAO channelDAO = INSTANCE.channelDAO();
                RoleDAO roleDAO = INSTANCE.roleDAO();
                UserDAO userDAO = INSTANCE.userDAO();
                MessageDAO messageDAO = INSTANCE.messageDAO();

//                communityDAO.addUser(new UserModel("admin"));

//                społeczność ktora po kliknieciu odpala dialog dołączenia do spolecznosci
////                testowe spolecznosci do wyswietlenia
//                communityDAO.addCommunity(new CommunityModel(1, "wedkowanie", 69517336201134080L, null, 224));
//                communityDAO.addCommunity(new CommunityModel(2, "tanie gruzy", 69517336201134080L, null, 224));
//                communityDAO.addCommunity(new CommunityModel(3, "klub swingers", 69517336201134080L, null, 224));
//                communityDAO.addCommunity(new CommunityModel("Spolecznosc 3"));
//                communityDAO.addCommunity(new CommunityModel("Spolecznosc 4"));
//                communityDAO.addCommunity(new CommunityModel("Spolecznosc 5"));
//
//                communityDAO.addChat(new ChatModel("czat tekst1 com3", 3));
//                communityDAO.addChat(new ChatModel("czat tekst2 com3", 3));
//                communityDAO.addChat(new ChatModel("czat tekst1 com2", 2));
//                communityDAO.addChat(new ChatModel("czat tekst2 com2", 2));
//                communityDAO.addChat(new ChatModel("czat tekst3 com3", 3));
//                communityDAO.addChat(new ChatModel("czat tekst1 com4", 4));
//                communityDAO.addChat(new ChatModel("czat tekst2 com4", 4));
//                communityDAO.addChat(new ChatModel("czat tekst3 com4", 4));
//                communityDAO.addChat(new ChatModel("czat tekst4 com4", 4));
//                communityDAO.addChat(new ChatModel("czat tekst1 com5", 5));
//                communityDAO.addChat(new ChatModel("czat tekst1 com6", 6));
//
//                communityDAO.addChannel(new ChannelModel("czat głosowy1 com3", 3));
//                communityDAO.addChannel(new ChannelModel("czat głosowy2 com3", 3));
//                communityDAO.addChannel(new ChannelModel("czat głosowy1 com2", 2));
//                communityDAO.addChannel(new ChannelModel("czat głosowy2 com2", 2));
//                communityDAO.addChannel(new ChannelModel("czat głosowy3 com3", 3));
//                communityDAO.addChannel(new ChannelModel("czat głosowy1 com4", 4));
//                communityDAO.addChannel(new ChannelModel("czat głosowy2 com4", 4));
//                communityDAO.addChannel(new ChannelModel("czat głosowy3 com4", 4));
//                communityDAO.addChannel(new ChannelModel("czat głosowy4 com4", 4));
//                communityDAO.addChannel(new ChannelModel("czat głosowy1 com5", 5));
//                communityDAO.addChannel(new ChannelModel("czat głosowy1 com6", 6));

//                communityDAO.addMessage(new MessageModel(1, "coś tam o górach", "22:31", "user1"));
//                communityDAO.addMessage(new MessageModel(1, "no góry, fajne, wysokie", "22:33", "user1"));
//                communityDAO.addMessage(new MessageModel(1, "generalnie", "22:35", "user1"));
            });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
//            TODO zweryfikować spolecznosci zapisane w bazie z tmyi pobranymi z serwera
//            tj. jak admin usunie z serwera to usumnąc z Room'ów u uzytkownikow
        }
    };
}



