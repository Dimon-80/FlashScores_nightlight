package com.example.flashscores_nightlight;

import android.util.Log;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DataHolder {
    private static final String LOG_TAG = "DataHolderLogs";
    public static Document doc;
    static ArrayList <ArrayList<String>> appDatasetFull = new ArrayList<>();
    static ArrayList <ArrayList<String>> appDatasetMatch = new ArrayList<>();
    public static String timeNowhms;

    static int numberOfMatches;

    public static void parcePage(){
        appDatasetFull.clear();
        appDatasetMatch.clear();
        AtomicInteger lineNumber_appDataSet = new AtomicInteger();
        AtomicInteger matchLineNumber_appDataSet = new AtomicInteger();
        try {
            Element leagues_live = doc.getElementsByClass("leagues--live").first();
            int leagues_live_numberOfLines = leagues_live.childrenSize();
            for (int i = 0; i < leagues_live_numberOfLines; i++) {
                if (leagues_live.child(i).className().equals("subTabs subTabs--myFsLabel")) {
                    Log.d(LOG_TAG, "date. lineNumber_appDataSet = " + lineNumber_appDataSet);
                    Log.d(LOG_TAG, "subTabs subTabs--myFsLabel: " + leagues_live.child(i).text());
                    ArrayList<String> date = new ArrayList<>();
                    date.add("date");
                    date.add(leagues_live.child(i).text());
                    appDatasetFull.add(date);

                    lineNumber_appDataSet.getAndIncrement();

                } else if (leagues_live.child(i).className().equals("event--section")) {
                    //Log.d(LOG_TAG, "event--section: " + leagues_live.child(i).text());
                    Elements event_section = leagues_live.child(i).children();
                    for (int numberOfLeagueCurrentDay = 0; numberOfLeagueCurrentDay < event_section.size(); numberOfLeagueCurrentDay++) {
                        Elements League = event_section.get(numberOfLeagueCurrentDay).children();
                        //Log.d(LOG_TAG, "League: " + League.text());
                        int numberOfLines = League.size();
                        for (int numberOfLineInLeague = 0; numberOfLineInLeague < numberOfLines; numberOfLineInLeague++) {
                            Log.d(LOG_TAG, "lineNumber_appDataSet = " + lineNumber_appDataSet);

                            Log.d(LOG_TAG, "numberOfLineInLeague = " + numberOfLineInLeague + " text: " + League.get(numberOfLineInLeague).text());
                            int numberOfCol = League.get(numberOfLineInLeague).childrenSize();
                            //Log.d(LOG_TAG, "numberOfCol: " + numberOfCol);


                            if (League.get(numberOfLineInLeague).getElementsByClass("event__title--name").hasText()) {
                                String event_title = League.get(numberOfLineInLeague).getElementsByClass("event__title--name").first().text();
                                Log.d(LOG_TAG, "event_title: " + event_title);

                                ArrayList<String> LeagueTitle = new ArrayList<>();
                                LeagueTitle.add("LeagueTitle");
                                LeagueTitle.add(event_title);
                                appDatasetFull.add(LeagueTitle);
                            } else {
                                /* ArrayList<String> match = [match_status (sheduled or Live or finished), participant_home,  participant_home_info, participant_away,  participant_away_info
                                , event_stage_time, event_score_home, event_score_away, american_football_serve] */
                                AtomicReference<String> match_status = new AtomicReference<>("match_status");
                                AtomicReference<String> participant_home = new AtomicReference<>("participant_home");
                                AtomicReference<String> participant_home_info = new AtomicReference<>("participant_home_info");
                                AtomicReference<String> participant_away = new AtomicReference<>("participant_away");
                                AtomicReference<String> participant_away_info = new AtomicReference<>("participant_away_info");
                                AtomicReference<String> event_stage_time = new AtomicReference<>("event_stage_time");
                                AtomicReference<String> event_score_home = new AtomicReference<>("_");
                                AtomicReference<String> event_score_away = new AtomicReference<>("_");
                                AtomicReference<String> american_football_serve = new AtomicReference<>("american_football_serve");


                                ArrayList<String> match = new ArrayList<>();
                                Element participant_home_element = null, participant_away_element = null;
                                if (League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--home").hasText()) {
                                    participant_home_element = League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--home").first();
                                    participant_home.set(League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--home").first().text());
                                    participant_home_info.set("normal");
                                    //match.add("match");
                                    //match.add(League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--home").first().text());
                                    //Log.d(LOG_TAG, "participant_home: " + participant_home);

                                } else if (League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--home event__participant--highlighted").hasText()) {
                                    participant_home_element = League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--home event__participant--highlighted").first();
                                    participant_home.set(League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--home event__participant--highlighted").first().text());
                                    participant_home_info.set("_highlighted");
                                    //Log.d(LOG_TAG, participant_home + participant_home_info);
                                } else if (League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--home fontBold").hasText()) {
                                    participant_home_element = League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--home fontBold").first();
                                    participant_home.set(League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--home fontBold").first().text());
                                    participant_home_info.set("_fontBold");
                                    //Log.d(LOG_TAG, participant_home + participant_home_info);
                                } else {
                                    Log.d(LOG_TAG, "participant_home niht");
                                }


                                if (League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--away").hasText()) {
                                    participant_away_element = League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--away").first();
                                    participant_away.set(League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--away").first().text());
                                    participant_away_info.set("normal");
                                    //Log.d(LOG_TAG, c3);

                                } else if (League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--away event__participant--highlighted").hasText()) {
                                    participant_away_element = League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--away event__participant--highlighted").first();
                                    participant_away.set(League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--away event__participant--highlighted").first().text());
                                    participant_away_info.set("_highlighted");
                                    //Log.d(LOG_TAG, c3 + "_highlighted");
                                } else if (League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--away fontBold").hasText()) {
                                    participant_away_element = League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--away fontBold").first();
                                    participant_away.set(League.get(numberOfLineInLeague).getElementsByClass("event__participant event__participant--away fontBold").first().text());
                                    participant_away_info.set("_fontBold");
                                    //Log.d(LOG_TAG, c3 + "_fontBold");
                                } else {
                                    Log.d(LOG_TAG, "c3 niht");
                                }

                                if (League.get(numberOfLineInLeague).getElementsByClass("event__stage").hasText() && !League.get(numberOfLineInLeague).getElementsByClass("event__stage").text().equals("")) {
                                    String event_stage = League.get(numberOfLineInLeague).getElementsByClass("event__stage").text();
                                    event_stage_time.set(League.get(numberOfLineInLeague).getElementsByClass("event__stage").text());
                                    match_status.set("live");
                                /*if (League.get(numberOfLineInLeague).getElementsByClass("event__stage").text().equals("")) {
                                    //event_stage_time.set("FIN");
                                    Log.d(LOG_TAG, "proebel");
                                }*/

                                    //Log.d(LOG_TAG, "1");
                                    Log.d(LOG_TAG, "event_stage = " + event_stage + "|");
                                }
                                //else {Log.d(LOG_TAG, "event_stage niht");}


                                else if (League.get(numberOfLineInLeague).getElementsByClass("event__time").hasText()) {
                                    String event_time = League.get(numberOfLineInLeague).getElementsByClass("event__time").text();
                                    event_stage_time.set(League.get(numberOfLineInLeague).getElementsByClass("event__time").text());
                                    match_status.set("sheduled");
                                    //Log.d(LOG_TAG, "2");
                                    Log.d(LOG_TAG, "event_time = " + event_time);
                                } else {
                                    event_stage_time.set("FIN");
                                    match_status.set("finished");
                                    Log.d(LOG_TAG, "event_time niht");
                                }

                                if (League.get(numberOfLineInLeague).getElementsByClass("event__score event__score--home").hasText()) {
                                    String c4 = League.get(numberOfLineInLeague).getElementsByClass("event__score event__score--home").first().text();
                                    event_score_home.set(League.get(numberOfLineInLeague).getElementsByClass("event__score event__score--home").first().text());
                                    Log.d(LOG_TAG, "event_score-home: " + c4);
                                } else {
                                    Log.d(LOG_TAG, "event_score-home niht");
                                }

                                if (League.get(numberOfLineInLeague).getElementsByClass("event__score event__score--away").hasText()) {
                                    String c5 = League.get(numberOfLineInLeague).getElementsByClass("event__score event__score--away").first().text();
                                    event_score_away.set(League.get(numberOfLineInLeague).getElementsByClass("event__score event__score--away").first().text());
                                    Log.d(LOG_TAG, "event_score-away: " + c5);
                                } else {
                                    Log.d(LOG_TAG, "event_score_away niht");
                                }

                                if (League.get(numberOfLineInLeague).children().hasClass("american-football___3_bBGvB icon--serveHome")) {
                                    american_football_serve.set("serveHome");
                                    //String event_time = League.child(i).getElementsByClass("event__time").text();
                                    Log.d(LOG_TAG, "serveHome");
                                } else if (League.get(numberOfLineInLeague).children().hasClass("american-football___3_bBGvB icon--serveAway")) {
                                    american_football_serve.set("serveAway");
                                    //String event_time = League.child(i).getElementsByClass("event__time").text();
                                    Log.d(LOG_TAG, "serveAway");
                                } else {
                                    american_football_serve.set("serveUndefined");
                                    Log.d(LOG_TAG, "serveUndefined");
                                }

                                if (participant_home_element != null && participant_home_element.children().hasClass("card-ico icon--redCard")) {
                                    american_football_serve.set("home_red_card");
                                    //String event_time = League.child(i).getElementsByClass("event__time").text();
                                    Log.d(LOG_TAG, "home_red_card");
                                } if (participant_away_element != null && participant_away_element.children().hasClass("card-ico icon--redCard")) {
                                    american_football_serve.set("away_red_card");
                                    //String event_time = League.child(i).getElementsByClass("event__time").text();
                                    Log.d(LOG_TAG, "away_red_card");
                                } if (participant_away_element != null && participant_away_element.children().hasClass("card-ico icon--redCard")
                                        && participant_home_element != null && participant_home_element.children().hasClass("card-ico icon--redCard")) {
                                    american_football_serve.set("both_red_card");
                                    //String event_time = League.child(i).getElementsByClass("event__time").text();
                                    Log.d(LOG_TAG, "both_red_card");

                                }



                                /*ArrayList<String> match = new ArrayList<>();*/
                                match.add("match");
                                match.add(String.valueOf(match_status));
                                match.add(String.valueOf(participant_home));
                                match.add(String.valueOf(participant_home_info));
                                match.add(String.valueOf(participant_away));
                                match.add(String.valueOf(participant_away_info));
                                match.add(String.valueOf(event_stage_time));
                                match.add(String.valueOf(event_score_home));
                                match.add(String.valueOf(event_score_away));
                                match.add(String.valueOf(american_football_serve));
                                appDatasetFull.add(match);
                                appDatasetMatch.add(match);
                                matchLineNumber_appDataSet.getAndIncrement();
                            }
                            lineNumber_appDataSet.getAndIncrement();
                        }
                    }
                }
            }
        }
        catch (Exception e){
            Log.d(LOG_TAG, e.getMessage());
        }
        Calendar calNow = Calendar.getInstance();

        DateFormat dateFormatMMMyyyy = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        Date dateNow = calNow.getTime();
        SimpleDateFormat hms = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        timeNowhms = hms.format(dateNow);

        numberOfMatches = matchLineNumber_appDataSet.get();
        for(int i = 0; i < appDatasetFull.size(); i++ ){
            Log.d(LOG_TAG, String.valueOf(appDatasetFull.get(i)));
        }
        Log.d(LOG_TAG, String.valueOf(appDatasetFull));


        /*Elements event_section = doc.getElementsByClass("event--section");


        int numberOfLeagues = event_section.get(0).childrenSize();
        Element League = event_section.get(0).child(0);
        Element League2 = event_section.get(1).child(0);
        int numberOfLines2 = League2.childrenSize();
        Log.d(LOG_TAG, "numberOfLines2; " + numberOfLines2);
        Log.d(LOG_TAG, "League2.text(): " + League2.text());
        int numberOfLines = League.childrenSize();
        //int numberOfLines = event_section.get(0).child(0).childrenSize();
        //int numberOfLines = event_section.get(0).child(0).childrenSize();
        Log.d(LOG_TAG, "numberOfLines; " + numberOfLines);

        for (int i = 0; i < numberOfLines; i++){
            Log.d(LOG_TAG, "i = " + i + " text: " + League.child(i).text());
            int numberOfCol = League.child(i).childrenSize();
            Log.d(LOG_TAG, "numberOfCol: " + numberOfCol);
            appDataset.add(i, new ArrayList<String>());

            if (League.child(i).getElementsByClass("event__title--name").hasText()){
                String event_title = League.child(i).getElementsByClass("event__title--name").first().text();
                Log.d(LOG_TAG, "event_title: " + event_title);
            }
            else{
                Log.d(LOG_TAG, "c1 niht");
            }

            if (League.child(i).getElementsByClass("event__participant event__participant--home").hasText()){
                String c2 = League.child(i).getElementsByClass("event__participant event__participant--home").first().text();
                Log.d(LOG_TAG, c2);
                appDataset.get(i).add(c2);
            }
            else if (League.child(i).getElementsByClass("event__participant event__participant--home event__participant--highlighted").hasText()){
                String c2 = League.child(i).getElementsByClass("event__participant event__participant--home event__participant--highlighted").first().text();
                Log.d(LOG_TAG, c2 + "_highlighted");
            }
            else if (League.child(i).getElementsByClass("event__participant event__participant--home fontBold").hasText()){
                String c2 = League.child(i).getElementsByClass("event__participant event__participant--home fontBold").first().text();
                Log.d(LOG_TAG, c2 + "_fontBold");
            }
            else{
                Log.d(LOG_TAG, "c2 niht");
            }

            if (League.child(i).getElementsByClass("event__participant event__participant--away").hasText()){
                String c3 = League.child(i).getElementsByClass("event__participant event__participant--away").first().text();
                Log.d(LOG_TAG, c3);
                appDataset.get(i).add(c3);
            }
            else if (League.child(i).getElementsByClass("event__participant event__participant--away event__participant--highlighted").hasText()){
                String c3 = League.child(i).getElementsByClass("event__participant event__participant--away event__participant--highlighted").first().text();
                Log.d(LOG_TAG, c3 + "_highlighted");
            }
            else if (League.child(i).getElementsByClass("event__participant event__participant--away event__participant fontBold").hasText()){
                String c3 = League.child(i).getElementsByClass("event__participant event__participant--away event__participant fontBold").first().text();
                Log.d(LOG_TAG, c3 + "_fontBold");
            }
            else{
                Log.d(LOG_TAG, "c3 niht");
            }

            if (League.child(i).getElementsByClass("event__stage").hasText()){
                String event_stage = League.child(i).getElementsByClass("event__stage").text();
                if (League.child(i).getElementsByClass("event__stage").text().equals("")){
                    Log.d(LOG_TAG, "proebel");
                }
                Log.d(LOG_TAG, "1");
                Log.d(LOG_TAG, "event_stage = " + event_stage + "|");
            }
            else{
                Log.d(LOG_TAG, "event_stage niht");
            }

            if (League.child(i).children().hasClass("american-football___3_bBGvB icon--serveHome")){
                //String event_time = League.child(i).getElementsByClass("event__time").text();
                Log.d(LOG_TAG, "serveHome");
            }
            else if (League.child(i).children().hasClass("american-football___3_bBGvB icon--serveAway")){
                //String event_time = League.child(i).getElementsByClass("event__time").text();
                Log.d(LOG_TAG, "serveAway");
            }
            else{
                Log.d(LOG_TAG, "huynja kakaja-to");
            }

            if (League.child(i).getElementsByClass("event__time").hasText()){
                String event_time = League.child(i).getElementsByClass("event__time").text();
                Log.d(LOG_TAG, "2");
                Log.d(LOG_TAG, "event_time = " + event_time);
            }
            else{
                Log.d(LOG_TAG, "event_time niht");
            }

            if (League.child(i).getElementsByClass("event__score event__score--home").hasText()){
                String c4 = League.child(i).getElementsByClass("event__score event__score--home").first().text();
                Log.d(LOG_TAG, "event_score-home: " + c4);
            }
            else{
                Log.d(LOG_TAG, "c4 niht");
            }

            if (League.child(i).getElementsByClass("event__score event__score--away").hasText()){
                String c5 = League.child(i).getElementsByClass("event__score event__score--away").first().text();
                Log.d(LOG_TAG, "event_score-away: " + c5);
            }
            else{
                Log.d(LOG_TAG, "c5 niht");
            }
        }*/
        //Log.d(LOG_TAG, String.valueOf(appDataset));



/*        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                event_stage_1 = findViewById(R.id.event_stage_1);
                participant_home_1 = findViewById(R.id.participant_home_1);
                //participant_home_1.setText("HUY");
                score_1 = findViewById(R.id.score_1);
                participant_away_1 = findViewById(R.id.participant_away_1);
                participant_home_1.setText(appDataset.get(1).get(0));
                participant_away_1.setText(appDataset.get(1).get(1));




            }
        })
        This shit not working here*/
    }
}
