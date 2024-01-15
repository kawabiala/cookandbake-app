package com.pingwinek.jens.cookandbake.migration

import org.json.JSONArray
import org.json.JSONObject

class DataMigration {

    companion object {
        inline fun <reified T> JSONArray.iterate(iteration: (T, Int) -> Unit) {
            for (i in 0 until this.length()) {
                val item = this.get(i)
                if (item != null && item is T) {
                    iteration(item, i)
                }
            }
        }

        fun getOldUserId(uid: String): String? {
            var id: String? = null
            users.iterate <JSONObject> { user, _ ->
                if (user.getString("newId") == uid) {
                    id = user.getString("id")
                }
            }
            return id
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Dato from Strato MySql 13.1.24
        ////////////////////////////////////////////////////////////////////////////////////////////

        /*
        NewId added with uid from firebase authentication
         */
        val users = JSONArray(
            "[\n" +
                    "{\"id\":\"1\",\"newId\":\"LoOGamVWzuTBciTBAUtA7cx1CNH2\",\"email\":\"jens.reufsteck@gmail.com\",\"password\":\"\",\"confirmed\":\"1\",\"temp_code\":\"b57119de3bc558b2db67f17fca34e2c9\",\"temp_code_valid_until\":\"2019-10-24 20:58:46\",\"last_modified\":\"2019-10-24 20:58:46\"},\n" +
                    "{\"id\":\"8\",\"newId\":\"l4MQt98H2lZtvE6S5197I4FGKDc2\",\"email\":\"google@pingwinek.de\",\"password\":\"\",\"confirmed\":\"1\",\"temp_code\":\"1537d840cf526167e27fb5fccee29718\",\"temp_code_valid_until\":\"2020-02-28 13:06:16\",\"last_modified\":\"2020-02-28 13:06:16\"},\n" +
                    "{\"id\":\"9\",\"newId\":\"ItM6twwlHiQRTZ7tPPDIfkUuXl12\",\"email\":\"demopl@pingwinek.de\",\"password\":\"\",\"confirmed\":\"1\",\"temp_code\":\"98d68b6db51577dd7bc97b1359f26396\",\"temp_code_valid_until\":\"2020-06-03 20:48:40\",\"last_modified\":\"2020-06-03 20:48:40\"},\n" +
                    "{\"id\":\"11\",\"newId\":\"aQtsUDgr1KW8RGRjkRPfYlm8RFx2\",\"email\":\"simonreufsteck@web.de\",\"password\":\"\",\"confirmed\":\"1\",\"temp_code\":\"0a84603363936651f7359df62551562c\",\"temp_code_valid_until\":\"2020-07-05 21:59:05\",\"last_modified\":\"2020-07-05 21:59:05\"},\n" +
                    "{\"id\":\"12\",\"newId\":\"XvrqCeavTMXbA4B6Lzmz6GO8qD42\",\"email\":\"claudiareufsteck@gmx.de\",\"password\":\"\",\"confirmed\":\"1\",\"temp_code\":\"100d54e27d44dfb0feff76600e997294\",\"temp_code_valid_until\":\"2020-07-05 22:01:53\",\"last_modified\":\"2020-07-05 22:01:53\"}\n" +
                    "]"
        )
        val recipes = JSONArray(
            "[\n" +
                    "{\"id\":\"1\",\"user_id\":\"1\",\"title\":\"Lasagne\",\"description\":\"Originalrezept, italienisch\",\"instruction\":\"\",\"last_modified\":\"1593328934835\"},\n" +
                    "{\"id\":\"2\",\"user_id\":\"1\",\"title\":\"Pizza\",\"description\":\"150g Mehl pro Person\",\"instruction\":null,\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"3\",\"user_id\":\"1\",\"title\":\"Cannelloni\",\"description\":\"mit Spinat und Ricotta\",\"instruction\":\"\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"4\",\"user_id\":\"1\",\"title\":\"Falafel\",\"description\":\"Aus ganzen Kichererbsen\",\"instruction\":\"Kichererbsen mindestens 12h in Wasser einweichen. Dann alle Zutaten (außer Mehl) in der Nussmühle mahlen. Soviel Mehl dazu geben, dass ein Teig entsteht. Falafel formen und bei 170 bis 180 Grad frittieren. \",\"last_modified\":\"1612294702098\"},\n" +
                    "{\"id\":\"5\",\"user_id\":\"5\",\"title\":\"H\",\"description\":\"Thail\",\"instruction\":null,\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"6\",\"user_id\":\"1\",\"title\":\"Pfannkuchen\",\"description\":\"150g Mehl pro Person\",\"instruction\":\"\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"7\",\"user_id\":\"1\",\"title\":\"Schinkensahne-Sauce\",\"description\":\"Mit Parmesan \",\"instruction\":\"\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"8\",\"user_id\":\"1\",\"title\":\"Waffeln\",\"description\":\"150g Mehl pro Person\",\"instruction\":\"7-8 min. backen\",\"last_modified\":\"1591214770982\"},\n" +
                    "{\"id\":\"9\",\"user_id\":\"1\",\"title\":\"Käswähe\",\"description\":\"Schweiz\",\"instruction\":\"Für den Teig 250g Mehl, Butter, 1 Ei und Salz.\\nFür die Füllung 30g Mehl, Käse 2 Eier und Sahne.\\n180 Grad, 45 min. \",\"last_modified\":\"1591214680111\"},\n" +
                    "{\"id\":\"10\",\"user_id\":\"1\",\"title\":\"Griesbrei\",\"description\":\"2-3 Personen \",\"instruction\":\"Ca. 70-80g Gries auf 1 Liter Milch\",\"last_modified\":\"1591214630259\"},\n" +
                    "{\"id\":\"11\",\"user_id\":\"1\",\"title\":\"Griesschnitten\",\"description\":\"3 Personen \",\"instruction\":\"Ca. 100-110g Gries auf 1 Liter Milch \",\"last_modified\":\"1591214635661\"},\n" +
                    "{\"id\":\"12\",\"user_id\":\"1\",\"title\":\"Rote Beete gebraten\",\"description\":\"\",\"instruction\":\"\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"13\",\"user_id\":\"1\",\"title\":\"Spinatnudeln\",\"description\":\"\",\"instruction\":\"\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"14\",\"user_id\":\"1\",\"title\":\"Rote Linsen Curry\",\"description\":\"Mit Kokosmilch\",\"instruction\":\"Zwiebeln anschwitzen, dann Kartoffeln mitbraten. Gemüsebrühe und Kokosmilch angießen, Tomatenmark, Curry hinzufügen. 5-10 Minuten kochen lassen, dann Linsen dazu geben. \",\"last_modified\":\"1591214746821\"},\n" +
                    "{\"id\":\"15\",\"user_id\":\"1\",\"title\":\"Quiche Lorraine\",\"description\":\"\",\"instruction\":\"180 Grad, ca. 35 Minuten\",\"last_modified\":\"1591214734876\"},\n" +
                    "{\"id\":\"16\",\"user_id\":\"1\",\"title\":\"Pancakes\",\"description\":\"Amerikanisch, 2-3 Personen \",\"instruction\":\"Mind. 1 Ei pro Person, für 4 Personen ggf. auch doppelte Menge. Weniger Mehl. \",\"last_modified\":\"1611481217153\"},\n" +
                    "{\"id\":\"17\",\"user_id\":\"1\",\"title\":\"Kaiserschmarrn \",\"description\":\"\",\"instruction\":\"\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"18\",\"user_id\":\"1\",\"title\":\"Nudelteig\",\"description\":\"auch für Pieroggen\",\"instruction\":\"Am besten mit heißem Wasser \",\"last_modified\":\"1591214693975\"},\n" +
                    "{\"id\":\"19\",\"user_id\":\"1\",\"title\":\"Kopytka\",\"description\":\"\",\"instruction\":\"\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"20\",\"user_id\":\"1\",\"title\":\"Dampfnudeln\",\"description\":\"\",\"instruction\":\"Teig ca. 1h gehen lassen. \\n\\nAuf 2 große Töpfe oder Pfannen aufteilen. Ca. 30min. garen, bis Flüssigkeit verdampft bzw. aufgesogen. Dazu Vanillesauce. \",\"last_modified\":\"1591214577875\"},\n" +
                    "{\"id\":\"21\",\"user_id\":\"1\",\"title\":\"Vanillesauce \",\"description\":\"\",\"instruction\":\"Sahne mit Zucker und Vanilleschote aufkochen, ziehen lassen. Dann Eigelb unterrühren und nochmals erhitzen. \",\"last_modified\":\"1591214765986\"},\n" +
                    "{\"id\":\"22\",\"user_id\":\"1\",\"title\":\"Semmelknödel\",\"description\":\"Rezept von Norbert \",\"instruction\":\"5-10min. kochen \",\"last_modified\":\"1591214757831\"},\n" +
                    "{\"id\":\"23\",\"user_id\":\"1\",\"title\":\"Erdbeerauflauf\",\"description\":\"\",\"instruction\":\"Eigelb mit übrigen Zutaten verrühren. Eiweiß schlagen und unterheben. Erdbeeren  unterheben. Hälfte der Streusel in runde Form geben, dann Erdbeerquarkmasse darauf und mit restlichen Streuseln bedecken. Ca. 45-50 min. bei 180° backen. \",\"last_modified\":\"1593328855130\"},\n" +
                    "{\"id\":\"24\",\"user_id\":\"1\",\"title\":\"Maracujatorte\",\"description\":\"\",\"instruction\":\"2 Biskuitböden aus insgesamt 4 Eiern.\\n\\nFruchtspiegel aus 240ml Saft und 3 Gelatine Blättern.\\n\\nCreme aus 250ml Saft, 300g Joghurt, 400g Sahne, 8 Blatt Gelatine, Zucker.\\n\\nZur Verzierung 400g Sahne, 2 Sahnesteif, Zucker, Kokosraspeln. \",\"last_modified\":\"1591214688473\"},\n" +
                    "{\"id\":\"25\",\"user_id\":\"1\",\"title\":\"Karamellcreme\",\"description\":\"für Tarte u.ä.\",\"instruction\":\"\",\"last_modified\":\"1591214382988\"},\n" +
                    "{\"id\":\"26\",\"user_id\":\"1\",\"title\":\"Flammkuchen\",\"description\":\"Mit Pesto-Creme-Fraiche\",\"instruction\":\"3 Portionen (ca. 150g Mehl pro Person). Geht auch ohne Hefe\",\"last_modified\":\"1648666793343\"},\n" +
                    "{\"id\":\"27\",\"user_id\":\"1\",\"title\":\"Polentapuffer\",\"description\":\"4 Personen \",\"instruction\":\"Maismehl im Wasser kochen, danach übrige Zutaten einrühren. In der Pfanne backen. \\n\\nEher als Beilage geeignet, dann entsprechend kleinere Menge. Evtl. auch mit Paprika oder geriebener Karotte. \",\"last_modified\":\"1591214729629\"},\n" +
                    "{\"id\":\"28\",\"user_id\":\"1\",\"title\":\"Paella\",\"description\":\"\",\"instruction\":\"Hähnchen zerteilen, Brustfilet in Würfel schneiden, Unterschenkel und Flügelstück abtrennen, Fleisch vom Oberschenkel in Würfel schneiden. Alles anbraten. Ggf. reichen auch die Brustfilets.\\n\\nÜbrige Teile und Knochen mit etwas Salz kochen.\\n\\nPulpo in Ringe schneiden und ca. 1 Stunde kochen (kein Salz dazugeben).\\n\\nFür die Muscheln einige Zwiebelringe und etwas Knoblauch in Olivenöl anschwitzen, mit Weißwein angießen, Salz und etwas Zucker dazugeben. Muscheln in dem Sud ca. 15 Minuten kochen (die Muscheln öffnen sich nach kurzer Zeit, sind aber noch recht fest).\\n\\nTomaten mit kochendem Wasser übergießen und schälen. In Stücke schneiden. \\n\\nRestliche Zwiebelringe und Knoblauch in Olivenöl anschwitzen, dann Tomaten dazugeben und ein paar Minuten mitkochen. Reis dazugeben und mit Brühe von den Knochen und Sud von den Muscheln angießen. Salz, Pfeffer, Paprika und Safran dazugeben. Nach und nach Muscheln, Pulpo, Garnelen, Hähnchenfleisch und Erbsen dazugeben. \",\"last_modified\":\"1657384567315\"},\n" +
                    "{\"id\":\"29\",\"user_id\":\"1\",\"title\":\"Polenta\",\"description\":\"überbacken \",\"instruction\":\"Maismehl in kochendes Wasser geben und ca. 2 Minuten weiter kochen. Auf Brett gießen,  abkühlen lassen, dann Käse darüber streuen und in Vierecke schneiden. Diese auf ein Blech legen und bei 200 Grad überbacken.\\n\\nDazu Tomatensauce.\",\"last_modified\":\"1591214724541\"},\n" +
                    "{\"id\":\"30\",\"user_id\":\"1\",\"title\":\"Karotten-Ingwer-Suppe\",\"description\":\"\",\"instruction\":\"Zitronengras, Ingwer und Korianderwurzel klein hacken und anbraten. Frühlingszwiebeln dazu geben und mitbraten. Karotten und Petersilienwurzel dazugeben. Kokosmilch und etwas Wasser angießen und kochen. Zuletzt Korianderblätter kleinhacken, dazugeben. Mit Mixer parieren und mit Limettensaft, Salz, Pfeffer und Zucker würzen. \",\"last_modified\":\"1591214668671\"},\n" +
                    "{\"id\":\"31\",\"user_id\":\"1\",\"title\":\"Ofengemüse\",\"description\":\"\",\"instruction\":\"Reichlich Olivenöl mit Gewürzen zu Marinade vermischen. Gemüse kleinschneiden und in der Marinade ca. 15 min. einlegen. Gut durchmischen. Dann Gemüse auf einem Blech verteilen und bei 200 Grad  20 min. im Ofen backen. \",\"last_modified\":\"1591214702511\"},\n" +
                    "{\"id\":\"72\",\"user_id\":\"1\",\"title\":\"Weinschaumsauce\",\"description\":\"\",\"instruction\":\"Alles zusammen verrühren und aufkochen. \",\"last_modified\":\"1591214775725\"},\n" +
                    "{\"id\":\"81\",\"user_id\":\"9\",\"title\":\"Pierogi Ruskie\",\"description\":\"\",\"instruction\":\"\",\"last_modified\":\"1591210200180\"},\n" +
                    "{\"id\":\"82\",\"user_id\":\"9\",\"title\":\"Nale?niki z Jagodami\",\"description\":\"Ze ?mietan? \",\"instruction\":\"\",\"last_modified\":\"1591210451435\"},\n" +
                    "{\"id\":\"83\",\"user_id\":\"9\",\"title\":\"Pizza Napoletańska\",\"description\":\"Wed?ug babci z Mediolanu\",\"instruction\":\"\",\"last_modified\":\"1591210311149\"},\n" +
                    "{\"id\":\"84\",\"user_id\":\"9\",\"title\":\"Barszcz po ukrainsku\",\"description\":\"\",\"instruction\":\"\",\"last_modified\":\"1591210402970\"},\n" +
                    "{\"id\":\"85\",\"user_id\":\"9\",\"title\":\"Kluski ?l?skie\",\"description\":\"Oryginalny przepis\",\"instruction\":\"\",\"last_modified\":\"1591210519868\"},\n" +
                    "{\"id\":\"86\",\"user_id\":\"9\",\"title\":\"Ros\",\"description\":\"Z makaronem domowym\",\"instruction\":\"\",\"last_modified\":\"1591210598801\"},\n" +
                    "{\"id\":\"87\",\"user_id\":\"9\",\"title\":\"De Volaille\",\"description\":\"\",\"instruction\":\"\",\"last_modified\":\"1591210632712\"},\n" +
                    "{\"id\":\"88\",\"user_id\":\"9\",\"title\":\"Pol?dwica\",\"description\":\"Rodzinny przepis\",\"instruction\":\"\",\"last_modified\":\"1591210751829\"},\n" +
                    "{\"id\":\"89\",\"user_id\":\"9\",\"title\":\"Makowiec\",\"description\":\"\",\"instruction\":\"\",\"last_modified\":\"1591210786403\"},\n" +
                    "{\"id\":\"90\",\"user_id\":\"9\",\"title\":\"Krupnik \",\"description\":\"\",\"instruction\":\"\",\"last_modified\":\"1591211082341\"},\n" +
                    "{\"id\":\"91\",\"user_id\":\"12\",\"title\":\"Dresdener Eierschecke\",\"description\":\"Eierschecke\",\"instruction\":\"\",\"last_modified\":\"1593979373314\"},\n" +
                    "{\"id\":\"92\",\"user_id\":\"1\",\"title\":\"Tacos\",\"description\":\"Vegetarische Füllung \",\"instruction\":\"Zwiebel anschwitzen mit roten Linsen kochen, Mais und Kidney-Bohnen dazu tun.\\n\\nTaco-Shell, Blätter vom Eisbergsalat, evtl. Reis, Cheddar gerieben, ggf. Zwiebelringe auf den Tisch stellen.\\n\\nDazu Guacamole, Sour Cream und Salsa.\",\"last_modified\":\"1599979846525\"},\n" +
                    "{\"id\":\"93\",\"user_id\":\"1\",\"title\":\"Salsa\",\"description\":\"z.B. zu Tacos\",\"instruction\":\"Chili kann man auch ganz weglassen\",\"last_modified\":\"1599979977944\"},\n" +
                    "{\"id\":\"94\",\"user_id\":\"1\",\"title\":\"Sour Cream\",\"description\":\"z.B. zu Tacos\",\"instruction\":\"Zwiebel nach Geschmack \",\"last_modified\":\"1599980088058\"},\n" +
                    "{\"id\":\"95\",\"user_id\":\"1\",\"title\":\"Guacamole\",\"description\":\"zu Tacos \",\"instruction\":\"\",\"last_modified\":\"1599980108949\"},\n" +
                    "{\"id\":\"96\",\"user_id\":\"1\",\"title\":\"Muscheln\",\"description\":\"in Weißwein\",\"instruction\":\"Pro kg Muscheln 100ml Gemüsebrühe und 100ml Weißwein. Ca. 10min kochen lassen.\",\"last_modified\":\"1605367900168\"},\n" +
                    "{\"id\":\"97\",\"user_id\":\"1\",\"title\":\"Pesto Walnuss\",\"description\":\"\",\"instruction\":\"\",\"last_modified\":\"1613814029317\"},\n" +
                    "{\"id\":\"98\",\"user_id\":\"1\",\"title\":\"Cannelloni\",\"description\":\"Mit Ricotta und Spinat\",\"instruction\":\"Tomatensauce aus Zwiebeln, Knoblauch und passierten Tomaten bereiten. Mit Estragon würzen. Füllung aus Ricotta, aufgekocht Spinat und gehackten Pistazien oder Pinienkernen bereiten. Teig aus Mehl, Ei und Wasser bereiten, Salz nicht vergessen.\\nTeig dünn ausrollen, in Vierecke schneiden, Füllung darauf geben und rollen. \\nTomatensauce und Cannelloni  in Auflaufform geben, sodass Cannelloni nicht verkleben. Mit geriebenem Käse bestreuen. \\nBei 180 Grad ca. 40min backen. \",\"last_modified\":\"1621804079368\"},\n" +
                    "{\"id\":\"99\",\"user_id\":\"1\",\"title\":\"Gefüllte Paprika vegetarisch \",\"description\":\"\",\"instruction\":\"Couscous mit doppelter Menge kochendem Wasser übergießen. \\nSpinat blanchieren und grob schneiden. \\nZwiebeln, Knoblauch, Chili und Zimt anbraten, passierte Tomaten dazugeben, mit Balsamico, Zucker und Salz würzen, kochen lassen. \\nFeta kleinschneiden, Spinat, Pinienkerne und soviel Couscous dazugeben, dass es zur Füllung der Paprika reicht, mit Salz und Pfeffer würzen. \\nPaprika füllen und mit Tomatensauce und Thymianzweigen in Auflaufform geben. Bei 180 Grad Ober- und Unterhitze ca. 50min. in den Ofen. \",\"last_modified\":\"1644700224477\"},\n" +
                    "{\"id\":\"100\",\"user_id\":\"12\",\"title\":\"Thai SUPPE\",\"description\":\"\",\"instruction\":\"Hühnerfleisch klein schneiden und anbraten, Frühlingszwiebeln in Ringe, den Ingwer in kleine Würfel schneiden und mit dem Huhn anbraten. Mit der Hühnerbrühe ablöschen. Kokosmilch, Sojasauce und Currypaste hinzufügen. Zitronengras längs einschneiden und in die Suppe geben. 5 min kochen, dann das restliche Gemüse und die Gewürze hinzufügen. Die Nudeln zum Schluss dazugeben. Fertig.....\",\"last_modified\":\"1646510965328\"},\n" +
                    "{\"id\":\"101\",\"user_id\":\"1\",\"title\":\"Sepia\",\"description\":\"Mit Kirschtomaten und grünem Spargel\",\"instruction\":\"Sepia in Ringe bzw. kleine Stücke schneiden und ca. 1 Stunde kochen. Grünen Spargel in Stücke schneiden und blanchieren. Zwiebel anschwitzen, dann grünen Spargel mitbraten, je nach Saison Steinpilze oder weiteres Gemüse zugeben. Sepia abseihen, dann in Olivenöl anbraten, Knoblauch mit anbraten, dann zum Gemüse dazugeben. Zum Schluss die halbierten Kirschtomaten hinzufügen. Dazu passen Linguine mit Trüffelöl.\",\"last_modified\":\"1666546672219\"},\n" +
                    "{\"id\":\"102\",\"user_id\":\"1\",\"title\":\"Zwiebelmarmelade\",\"description\":\"\",\"instruction\":\"\",\"last_modified\":\"1667850344861\"},\n" +
                    "{\"id\":\"103\",\"user_id\":\"1\",\"title\":\"Paella vegetarisch\",\"description\":\"\",\"instruction\":\"Aus Tomaten, einer Zwiebel und Knoblauch Sugo bereiten, darin Reis garen. \\n\\nParallel 1-2 Zwiebeln in Ringen andünsten, Karotte mitbraten, beides dann zum Reis hinzufügen. Mit Salz, Zucker, geräuchertem Paprika, Kurkuma und Pfeffer würzen. \\n\\nNach und nach Champignons, Brokkoli und Spinat dazugeben und mitkochen. Zum Schluss rote Paprika, Erbsen und Safran dazugeben. \",\"last_modified\":\"1668457037038\"},\n" +
                    "{\"id\":\"104\",\"user_id\":\"1\",\"title\":\"Fenchel-Steckrüben-Gemüse\",\"description\":\"\",\"instruction\":\"Steckrübe in feine Streifen schneiden. Erst Fenchel andünsten, dann Steckrübe dazugeben. Ggf. etwas Creme Fraiche. \",\"last_modified\":\"1676109697308\"},\n" +
                    "{\"id\":\"105\",\"user_id\":\"1\",\"title\":\"Calamar\",\"description\":\"Mit Thai-Spargel\",\"instruction\":\"Calamar in feine Streifen schneiden. Ca. 5-10 Minuten anbraten. Thai-Spargel und Knoblauch ca. 5 min. mitanbraten. Zum Schluss halbierte Kirschtomaten dazugeben. \",\"last_modified\":\"1676109884936\"},\n" +
                    "{\"id\":\"106\",\"user_id\":\"1\",\"title\":\"Quiche mit Zucchini und Feta\",\"description\":\"\",\"instruction\":\"Bei 180 Grad ca. 40min. backen. \",\"last_modified\":\"1693165789136\"},\n" +
                    "{\"id\":\"109\",\"user_id\":\"1\",\"title\":\"Sri-Lanka Curry\",\"description\":\"\",\"instruction\":\"\",\"last_modified\":\"1699254448500\"},\n" +
                    "{\"id\":\"110\",\"user_id\":\"1\",\"title\":\"Dorado aus dem Ofen\",\"description\":\"\",\"instruction\":\"Reicht für 2 Personen. Mit Rosmarin, Thymian, Olivenöl, Zitronensaft, Salz im Ofen backen. Bei 200 Grad Umluft ca. 35min.\",\"last_modified\":\"1699731391279\"},\n" +
                    "{\"id\":\"111\",\"user_id\":\"1\",\"title\":\"Wedges\",\"description\":\"\",\"instruction\":\"Je nach Menge und Größe ca. 1h bei 180 bis 200 Grad Umluft. \",\"last_modified\":\"1699731444692\"},\n" +
                    "{\"id\":\"112\",\"user_id\":\"1\",\"title\":\"Hähnchenbrust mit Paprika\",\"description\":\"\",\"instruction\":\"Zwiebeln und Paprika scharf anbraten, sodass Röstaromen entstehen. Gewürzgurken dazugeben. Mit Wasser, Gemüsebrühe und Schmand eine Sauce zubereiten. Tomatenmark und Gurkenwasser dazugeben. Mit Stärke andicken. Mit Paprika, Pfeffer und Salz würzen. Ggf. auch Räucherpaprika nutzen. \\n\\nHähnchenbrust in mehrere große Teile schneiden, mit der Sauce begießen und im Ofen ca. 40-50min. garen.\\n\\nDazu passen Wedges.\",\"last_modified\":\"1704749297669\"}\n" +
                    "]\n"
        )
        val ingredients = JSONArray(
            "[\n" +
                    "{\"id\":\"1\",\"recipe_id\":\"6\",\"quantity\":\"150\",\"quantity_verbal\":null,\"unity\":\"gr.\",\"name\":\"Mehl\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"7\",\"recipe_id\":\"6\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Salz \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"8\",\"recipe_id\":\"4\",\"quantity\":\"200\",\"quantity_verbal\":\"\",\"unity\":\"Gr\",\"name\":\"Kichererbsen\",\"last_modified\":\"1613210875403\"},\n" +
                    "{\"id\":\"9\",\"recipe_id\":\"8\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Mehl\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"10\",\"recipe_id\":\"7\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Schinken\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"11\",\"recipe_id\":\"7\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"Zehe\",\"name\":\"Knoblauch \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"12\",\"recipe_id\":\"7\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"El\",\"name\":\"Parmesan \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"13\",\"recipe_id\":\"7\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Sahne\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"14\",\"recipe_id\":\"7\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Gemüsebrühe\",\"last_modified\":\"1591253672674\"},\n" +
                    "{\"id\":\"15\",\"recipe_id\":\"9\",\"quantity\":\"280\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Mehl \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"16\",\"recipe_id\":\"9\",\"quantity\":\"125\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Butter \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"17\",\"recipe_id\":\"9\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Salz\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"18\",\"recipe_id\":\"9\",\"quantity\":\"3\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Eier\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"19\",\"recipe_id\":\"9\",\"quantity\":\"250\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Emmentaler \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"20\",\"recipe_id\":\"9\",\"quantity\":\"300\",\"quantity_verbal\":null,\"unity\":\"ml\",\"name\":\"Sahne \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"21\",\"recipe_id\":\"0\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"l\",\"name\":\"Milch \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"22\",\"recipe_id\":\"10\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"l\",\"name\":\"Milch \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"23\",\"recipe_id\":\"10\",\"quantity\":\"70\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Gries\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"24\",\"recipe_id\":\"10\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Ei\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"25\",\"recipe_id\":\"11\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"l\",\"name\":\"Milch \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"26\",\"recipe_id\":\"11\",\"quantity\":\"110\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Gries \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"28\",\"recipe_id\":\"11\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"EL \",\"name\":\"Zucker\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"29\",\"recipe_id\":\"12\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Rote Bete, mittelgroß\",\"last_modified\":\"1591253654283\"},\n" +
                    "{\"id\":\"30\",\"recipe_id\":\"10\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"Prise\",\"name\":\"Salz\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"31\",\"recipe_id\":\"14\",\"quantity\":\"400\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Kartoffeln\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"32\",\"recipe_id\":\"14\",\"quantity\":\"200\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Rote Linsen\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"33\",\"recipe_id\":\"14\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Olivenöl\",\"last_modified\":\"1591253666017\"},\n" +
                    "{\"id\":\"34\",\"recipe_id\":\"14\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Frühlingszwiebeln\",\"last_modified\":\"1591253663629\"},\n" +
                    "{\"id\":\"35\",\"recipe_id\":\"14\",\"quantity\":\"2\",\"quantity_verbal\":null,\"unity\":\"EL\",\"name\":\"Curry\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"36\",\"recipe_id\":\"14\",\"quantity\":\"2\",\"quantity_verbal\":null,\"unity\":\"TL\",\"name\":\"Tomatenmark\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"37\",\"recipe_id\":\"14\",\"quantity\":\"400\",\"quantity_verbal\":null,\"unity\":\"ml\",\"name\":\"Kokosmilch\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"38\",\"recipe_id\":\"14\",\"quantity\":\"250\",\"quantity_verbal\":\"\",\"unity\":\"ml\",\"name\":\"Gemüsebrühe\",\"last_modified\":\"1591253659752\"},\n" +
                    "{\"id\":\"39\",\"recipe_id\":\"15\",\"quantity\":\"250\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Mehl\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"40\",\"recipe_id\":\"15\",\"quantity\":\"160\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Margarine\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"41\",\"recipe_id\":\"15\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Ei\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"42\",\"recipe_id\":\"15\",\"quantity\":\"100\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Speck\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"43\",\"recipe_id\":\"15\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Zwiebel\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"44\",\"recipe_id\":\"15\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Lauch\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"45\",\"recipe_id\":\"15\",\"quantity\":\"4\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Eier\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"46\",\"recipe_id\":\"15\",\"quantity\":\"250\",\"quantity_verbal\":null,\"unity\":\"ml\",\"name\":\"Sahne\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"47\",\"recipe_id\":\"16\",\"quantity\":\"250\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Mehl \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"48\",\"recipe_id\":\"16\",\"quantity\":\"150\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Buttermilch \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"49\",\"recipe_id\":\"16\",\"quantity\":\"30\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Zucker \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"50\",\"recipe_id\":\"16\",\"quantity\":\"3\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Eier \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"51\",\"recipe_id\":\"16\",\"quantity\":\"3\",\"quantity_verbal\":\"\",\"unity\":\"TL\",\"name\":\"Öl \",\"last_modified\":\"1591253564330\"},\n" +
                    "{\"id\":\"52\",\"recipe_id\":\"16\",\"quantity\":\"3\",\"quantity_verbal\":null,\"unity\":\"TL\",\"name\":\"Backpulver \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"53\",\"recipe_id\":\"16\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Salz \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"54\",\"recipe_id\":\"17\",\"quantity\":\"6\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Eigelb\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"55\",\"recipe_id\":\"17\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Vanillezucker \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"56\",\"recipe_id\":\"17\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"EL\",\"name\":\"Zucker \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"57\",\"recipe_id\":\"17\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Salz \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"58\",\"recipe_id\":\"17\",\"quantity\":\"250\",\"quantity_verbal\":null,\"unity\":\"gr\",\"name\":\"Mehl \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"59\",\"recipe_id\":\"17\",\"quantity\":\"500\",\"quantity_verbal\":null,\"unity\":\"ml\",\"name\":\"Milch \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"60\",\"recipe_id\":\"17\",\"quantity\":\"6\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Eiweiß \",\"last_modified\":\"1591253504984\"},\n" +
                    "{\"id\":\"61\",\"recipe_id\":\"18\",\"quantity\":\"500\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Mehl \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"62\",\"recipe_id\":\"18\",\"quantity\":\"2\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Eier \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"63\",\"recipe_id\":\"18\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"Glas\",\"name\":\"Wasser \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"64\",\"recipe_id\":\"18\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Salz \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"65\",\"recipe_id\":\"19\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"kg\",\"name\":\"Kartoffeln \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"66\",\"recipe_id\":\"19\",\"quantity\":\"200\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Mehl \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"67\",\"recipe_id\":\"19\",\"quantity\":\"250\",\"quantity_verbal\":\"\",\"unity\":\"g\",\"name\":\"Schichtkäse \",\"last_modified\":\"1591253536450\"},\n" +
                    "{\"id\":\"68\",\"recipe_id\":\"19\",\"quantity\":\"100\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Kartoffelmehl \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"69\",\"recipe_id\":\"19\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Ei \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"70\",\"recipe_id\":\"19\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Salz \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"71\",\"recipe_id\":\"20\",\"quantity\":\"500\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Mehl \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"72\",\"recipe_id\":\"20\",\"quantity\":\"20\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Hefe \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"73\",\"recipe_id\":\"20\",\"quantity\":\"50\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Zucker \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"74\",\"recipe_id\":\"20\",\"quantity\":\"125\",\"quantity_verbal\":null,\"unity\":\"ml\",\"name\":\"Milch \",\"last_modified\":\"1588177030285\"},\n" +
                    "{\"id\":\"75\",\"recipe_id\":\"20\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Salz \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"76\",\"recipe_id\":\"20\",\"quantity\":\"80\",\"quantity_verbal\":null,\"unity\":\"g\",\"name\":\"Butter, zerlassen \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"77\",\"recipe_id\":\"20\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Ei\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"78\",\"recipe_id\":\"20\",\"quantity\":\"100\",\"quantity_verbal\":\"\",\"unity\":\"g\",\"name\":\"Butter für die Pfanne\",\"last_modified\":\"1591253470661\"},\n" +
                    "{\"id\":\"79\",\"recipe_id\":\"20\",\"quantity\":\"250\",\"quantity_verbal\":\"\",\"unity\":\"ml\",\"name\":\"Milch für die Pfanne \",\"last_modified\":\"1591253468369\"},\n" +
                    "{\"id\":\"80\",\"recipe_id\":\"20\",\"quantity\":\"100\",\"quantity_verbal\":\"\",\"unity\":\"g\",\"name\":\"Zucker für die Pfanne \",\"last_modified\":\"1591253464898\"},\n" +
                    "{\"id\":\"81\",\"recipe_id\":\"21\",\"quantity\":\"250\",\"quantity_verbal\":null,\"unity\":\"ml\",\"name\":\"Sahne \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"82\",\"recipe_id\":\"21\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Vanilleschote\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"83\",\"recipe_id\":\"21\",\"quantity\":\"2\",\"quantity_verbal\":null,\"unity\":\"EL\",\"name\":\"Zucker \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"84\",\"recipe_id\":\"21\",\"quantity\":\"2\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Eigelb \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"85\",\"recipe_id\":\"22\",\"quantity\":\"8\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Semmeln, alt\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"86\",\"recipe_id\":\"22\",\"quantity\":\"3\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Eier \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"87\",\"recipe_id\":\"22\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Zwiebel, gedünstet \",\"last_modified\":\"1591253681491\"},\n" +
                    "{\"id\":\"88\",\"recipe_id\":\"22\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Speck \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"89\",\"recipe_id\":\"22\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Petersilie \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"90\",\"recipe_id\":\"22\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Mehl \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"91\",\"recipe_id\":\"22\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Semmelbrösel \",\"last_modified\":\"1591253678282\"},\n" +
                    "{\"id\":\"92\",\"recipe_id\":\"22\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Salz \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"93\",\"recipe_id\":\"22\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Pfeffer \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"94\",\"recipe_id\":\"6\",\"quantity\":\"1\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Ei\",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"95\",\"recipe_id\":\"6\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Milch \",\"last_modified\":\"0\"},\n" +
                    "{\"id\":\"96\",\"recipe_id\":\"23\",\"quantity\":\"500\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Quark\",\"last_modified\":\"1590237698202\"},\n" +
                    "{\"id\":\"97\",\"recipe_id\":\"23\",\"quantity\":\"4\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Eier\",\"last_modified\":\"1590237709712\"},\n" +
                    "{\"id\":\"99\",\"recipe_id\":\"23\",\"quantity\":\"20\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Gries\",\"last_modified\":\"1590237788111\"},\n" +
                    "{\"id\":\"100\",\"recipe_id\":\"30\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Karotten \",\"last_modified\":\"1586675205828\"},\n" +
                    "{\"id\":\"101\",\"recipe_id\":\"30\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Zitronengras\",\"last_modified\":\"1586675217965\"},\n" +
                    "{\"id\":\"102\",\"recipe_id\":\"30\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Ingwer\",\"last_modified\":\"1586675225856\"},\n" +
                    "{\"id\":\"103\",\"recipe_id\":\"30\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Korianderwurzel\",\"last_modified\":\"1586675244984\"},\n" +
                    "{\"id\":\"104\",\"recipe_id\":\"30\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Frühlingszwiebeln \",\"last_modified\":\"1591253524281\"},\n" +
                    "{\"id\":\"105\",\"recipe_id\":\"30\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Petersilienwurzel \",\"last_modified\":\"1586675275456\"},\n" +
                    "{\"id\":\"106\",\"recipe_id\":\"30\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Kokosmilch \",\"last_modified\":\"1586675281206\"},\n" +
                    "{\"id\":\"107\",\"recipe_id\":\"30\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Korianderblätter\",\"last_modified\":\"1591253518066\"},\n" +
                    "{\"id\":\"108\",\"recipe_id\":\"30\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Limettensaft \",\"last_modified\":\"1586675317801\"},\n" +
                    "{\"id\":\"109\",\"recipe_id\":\"30\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Salz\",\"last_modified\":\"1586675330586\"},\n" +
                    "{\"id\":\"110\",\"recipe_id\":\"30\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Pfeffer \",\"last_modified\":\"1586675336312\"},\n" +
                    "{\"id\":\"111\",\"recipe_id\":\"30\",\"quantity\":\"0\",\"quantity_verbal\":null,\"unity\":\"\",\"name\":\"Zucker \",\"last_modified\":\"1586675342449\"},\n" +
                    "{\"id\":\"127\",\"recipe_id\":\"72\",\"quantity\":\"250\",\"quantity_verbal\":\"\",\"unity\":\"ml\",\"name\":\"Wein\",\"last_modified\":\"1589559745831\"},\n" +
                    "{\"id\":\"128\",\"recipe_id\":\"72\",\"quantity\":\"2\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Eigelb\",\"last_modified\":\"1589559791746\"},\n" +
                    "{\"id\":\"129\",\"recipe_id\":\"72\",\"quantity\":\"100\",\"quantity_verbal\":\"\",\"unity\":\"g\",\"name\":\"Zucker\",\"last_modified\":\"1589559810767\"},\n" +
                    "{\"id\":\"130\",\"recipe_id\":\"72\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Zitronensaft\",\"last_modified\":\"1589559835327\"},\n" +
                    "{\"id\":\"131\",\"recipe_id\":\"72\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"TL\",\"name\":\"Stärkemehl\",\"last_modified\":\"1591253696220\"},\n" +
                    "{\"id\":\"132\",\"recipe_id\":\"23\",\"quantity\":\"50\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Butter, flüssig\",\"last_modified\":\"1591253482781\"},\n" +
                    "{\"id\":\"133\",\"recipe_id\":\"23\",\"quantity\":\"80\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Zucker\",\"last_modified\":\"1590237738870\"},\n" +
                    "{\"id\":\"134\",\"recipe_id\":\"23\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Vanillezucker\",\"last_modified\":\"1590237755837\"},\n" +
                    "{\"id\":\"135\",\"recipe_id\":\"23\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"Prise\",\"name\":\"Salz\",\"last_modified\":\"1590237774616\"},\n" +
                    "{\"id\":\"136\",\"recipe_id\":\"23\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Puddingpulver\",\"last_modified\":\"1590237807614\"},\n" +
                    "{\"id\":\"137\",\"recipe_id\":\"23\",\"quantity\":\"300\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Erdbeeren\",\"last_modified\":\"1590237825173\"},\n" +
                    "{\"id\":\"138\",\"recipe_id\":\"23\",\"quantity\":\"50\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Butter (Streusel)\",\"last_modified\":\"1590237862758\"},\n" +
                    "{\"id\":\"139\",\"recipe_id\":\"23\",\"quantity\":\"50\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Zucker (Streusel)\",\"last_modified\":\"1590237888436\"},\n" +
                    "{\"id\":\"140\",\"recipe_id\":\"23\",\"quantity\":\"100\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Mehl (Streusel)\",\"last_modified\":\"1590237908949\"},\n" +
                    "{\"id\":\"149\",\"recipe_id\":\"83\",\"quantity\":\"600\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"M?ka\",\"last_modified\":\"1591212807651\"},\n" +
                    "{\"id\":\"150\",\"recipe_id\":\"83\",\"quantity\":\"40\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Dro?d?e\",\"last_modified\":\"1591212072298\"},\n" +
                    "{\"id\":\"151\",\"recipe_id\":\"83\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"szkl\",\"name\":\"Woda\",\"last_modified\":\"1591211216627\"},\n" +
                    "{\"id\":\"152\",\"recipe_id\":\"83\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"opak.\",\"name\":\"Przecier Pomidorowy\",\"last_modified\":\"1591212129908\"},\n" +
                    "{\"id\":\"153\",\"recipe_id\":\"83\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Cebula\",\"last_modified\":\"1591212140170\"},\n" +
                    "{\"id\":\"154\",\"recipe_id\":\"83\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"z?b\",\"name\":\"Czosnek\",\"last_modified\":\"1591212172412\"},\n" +
                    "{\"id\":\"155\",\"recipe_id\":\"83\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Mozzarella\",\"last_modified\":\"1591212214258\"},\n" +
                    "{\"id\":\"156\",\"recipe_id\":\"28\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Hähnchen\",\"last_modified\":\"1591466246290\"},\n" +
                    "{\"id\":\"157\",\"recipe_id\":\"28\",\"quantity\":\"1\",\"quantity_verbal\":\"1 (ca. 150g)\",\"unity\":\"\",\"name\":\"Pulpo\",\"last_modified\":\"1591466290426\"},\n" +
                    "{\"id\":\"158\",\"recipe_id\":\"28\",\"quantity\":\"500\",\"quantity_verbal\":\"\",\"unity\":\"g\",\"name\":\"Muscheln\",\"last_modified\":\"1591466307278\"},\n" +
                    "{\"id\":\"159\",\"recipe_id\":\"28\",\"quantity\":\"250\",\"quantity_verbal\":\"\",\"unity\":\"g\",\"name\":\"Garnelen\",\"last_modified\":\"1591466343516\"},\n" +
                    "{\"id\":\"160\",\"recipe_id\":\"28\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Risotto-Reis\",\"last_modified\":\"1591466372392\"},\n" +
                    "{\"id\":\"161\",\"recipe_id\":\"28\",\"quantity\":\"3\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Tomaten\",\"last_modified\":\"1591466385125\"},\n" +
                    "{\"id\":\"162\",\"recipe_id\":\"28\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Zwiebel\",\"last_modified\":\"1591466393310\"},\n" +
                    "{\"id\":\"163\",\"recipe_id\":\"28\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Knoblauch\",\"last_modified\":\"1591466406691\"},\n" +
                    "{\"id\":\"164\",\"recipe_id\":\"28\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Safran\",\"last_modified\":\"1591468058958\"},\n" +
                    "{\"id\":\"165\",\"recipe_id\":\"28\",\"quantity\":\"1\",\"quantity_verbal\":\"1 kleine\",\"unity\":\"Dose\",\"name\":\"Erbsen\",\"last_modified\":\"1591468077136\"},\n" +
                    "{\"id\":\"166\",\"recipe_id\":\"92\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Taco Shells\",\"last_modified\":\"1599979496344\"},\n" +
                    "{\"id\":\"167\",\"recipe_id\":\"92\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"Dose\",\"name\":\"Kidney-Bohnen\",\"last_modified\":\"1599979521740\"},\n" +
                    "{\"id\":\"168\",\"recipe_id\":\"92\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"Dose (kl\",\"name\":\"Mais\",\"last_modified\":\"1599979543432\"},\n" +
                    "{\"id\":\"169\",\"recipe_id\":\"92\",\"quantity\":\"200\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Rote Linsen\",\"last_modified\":\"1599979590881\"},\n" +
                    "{\"id\":\"170\",\"recipe_id\":\"92\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Zwiebel\",\"last_modified\":\"1599979604458\"},\n" +
                    "{\"id\":\"171\",\"recipe_id\":\"92\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Eisbergsalat\",\"last_modified\":\"1599979618964\"},\n" +
                    "{\"id\":\"172\",\"recipe_id\":\"92\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Cheddar\",\"last_modified\":\"1599979627013\"},\n" +
                    "{\"id\":\"173\",\"recipe_id\":\"92\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Reis\",\"last_modified\":\"1599979639781\"},\n" +
                    "{\"id\":\"174\",\"recipe_id\":\"93\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Zwiebel\",\"last_modified\":\"1599979883885\"},\n" +
                    "{\"id\":\"175\",\"recipe_id\":\"93\",\"quantity\":\"4\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Fleischtomaten\",\"last_modified\":\"1599979898218\"},\n" +
                    "{\"id\":\"176\",\"recipe_id\":\"93\",\"quantity\":\"3\",\"quantity_verbal\":\"\",\"unity\":\"Zehen\",\"name\":\"Knoblauch\",\"last_modified\":\"1599979916128\"},\n" +
                    "{\"id\":\"177\",\"recipe_id\":\"93\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Limette (Saft)\",\"last_modified\":\"1599979937689\"},\n" +
                    "{\"id\":\"178\",\"recipe_id\":\"93\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Koriander\",\"last_modified\":\"1599979945236\"},\n" +
                    "{\"id\":\"179\",\"recipe_id\":\"93\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Chili\",\"last_modified\":\"1599979953321\"},\n" +
                    "{\"id\":\"180\",\"recipe_id\":\"94\",\"quantity\":\"500\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Quark\",\"last_modified\":\"1599980025307\"},\n" +
                    "{\"id\":\"181\",\"recipe_id\":\"94\",\"quantity\":\"200\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Creme Fraiche\",\"last_modified\":\"1599980042397\"},\n" +
                    "{\"id\":\"182\",\"recipe_id\":\"94\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Petersilie \",\"last_modified\":\"1599980053303\"},\n" +
                    "{\"id\":\"183\",\"recipe_id\":\"94\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Knoblauch \",\"last_modified\":\"1599980062628\"},\n" +
                    "{\"id\":\"184\",\"recipe_id\":\"94\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Zwiebel\",\"last_modified\":\"1599980070327\"},\n" +
                    "{\"id\":\"185\",\"recipe_id\":\"95\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Avocado\",\"last_modified\":\"1599980121764\"},\n" +
                    "{\"id\":\"186\",\"recipe_id\":\"95\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"El\",\"name\":\"Limette (Saft)\",\"last_modified\":\"1599980146938\"},\n" +
                    "{\"id\":\"187\",\"recipe_id\":\"95\",\"quantity\":\"0.5\",\"quantity_verbal\":\"1\\/2\",\"unity\":\"Zehe\",\"name\":\"Knoblauch\",\"last_modified\":\"1599980171884\"},\n" +
                    "{\"id\":\"188\",\"recipe_id\":\"95\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Koriander \",\"last_modified\":\"1599980183311\"},\n" +
                    "{\"id\":\"189\",\"recipe_id\":\"96\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"kg\",\"name\":\"Muscheln\",\"last_modified\":\"1605367928884\"},\n" +
                    "{\"id\":\"190\",\"recipe_id\":\"96\",\"quantity\":\"100\",\"quantity_verbal\":\"\",\"unity\":\"ml\",\"name\":\"Gemüsebrühe\",\"last_modified\":\"1605367948689\"},\n" +
                    "{\"id\":\"191\",\"recipe_id\":\"96\",\"quantity\":\"100\",\"quantity_verbal\":\"\",\"unity\":\"ml\",\"name\":\"Weißwein\",\"last_modified\":\"1605367961963\"},\n" +
                    "{\"id\":\"192\",\"recipe_id\":\"96\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Lorbeerblatt \",\"last_modified\":\"1605367968148\"},\n" +
                    "{\"id\":\"193\",\"recipe_id\":\"96\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Zwiebel \",\"last_modified\":\"1605367972616\"},\n" +
                    "{\"id\":\"194\",\"recipe_id\":\"96\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Knoblauch \",\"last_modified\":\"1605367979680\"},\n" +
                    "{\"id\":\"195\",\"recipe_id\":\"4\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Zwiebel\",\"last_modified\":\"1613210892700\"},\n" +
                    "{\"id\":\"196\",\"recipe_id\":\"4\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Petersilie \",\"last_modified\":\"1613210904371\"},\n" +
                    "{\"id\":\"197\",\"recipe_id\":\"4\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Koriander (Blätter)\",\"last_modified\":\"1613210943514\"},\n" +
                    "{\"id\":\"198\",\"recipe_id\":\"4\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Salz\",\"last_modified\":\"1613210954959\"},\n" +
                    "{\"id\":\"199\",\"recipe_id\":\"4\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Chilipulver\",\"last_modified\":\"1613210966894\"},\n" +
                    "{\"id\":\"200\",\"recipe_id\":\"4\",\"quantity\":\"4\",\"quantity_verbal\":\"\",\"unity\":\"Zehen\",\"name\":\"Knoblauch \",\"last_modified\":\"1613210981352\"},\n" +
                    "{\"id\":\"201\",\"recipe_id\":\"4\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Kreuzkümmel\",\"last_modified\":\"1613211000165\"},\n" +
                    "{\"id\":\"202\",\"recipe_id\":\"4\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"TL\",\"name\":\"Backpulver \",\"last_modified\":\"1613211020276\"},\n" +
                    "{\"id\":\"203\",\"recipe_id\":\"4\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Mehl\",\"last_modified\":\"1613211028933\"},\n" +
                    "{\"id\":\"204\",\"recipe_id\":\"97\",\"quantity\":\"250\",\"quantity_verbal\":\"\",\"unity\":\"Gr\",\"name\":\"Tomaten\",\"last_modified\":\"1613814063873\"},\n" +
                    "{\"id\":\"205\",\"recipe_id\":\"97\",\"quantity\":\"2\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Avocado\",\"last_modified\":\"1613814074845\"},\n" +
                    "{\"id\":\"206\",\"recipe_id\":\"97\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"Bund\",\"name\":\"Rucola\",\"last_modified\":\"1613814089533\"},\n" +
                    "{\"id\":\"207\",\"recipe_id\":\"97\",\"quantity\":\"60\",\"quantity_verbal\":\"\",\"unity\":\"Gr\",\"name\":\"Walnüsse\",\"last_modified\":\"1613814108800\"},\n" +
                    "{\"id\":\"208\",\"recipe_id\":\"97\",\"quantity\":\"60\",\"quantity_verbal\":\"\",\"unity\":\"Gr\",\"name\":\"Parmesan\",\"last_modified\":\"1613814120555\"},\n" +
                    "{\"id\":\"209\",\"recipe_id\":\"97\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Chili\",\"last_modified\":\"1613814125549\"},\n" +
                    "{\"id\":\"210\",\"recipe_id\":\"97\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Zitronensaft\",\"last_modified\":\"1613814140959\"},\n" +
                    "{\"id\":\"211\",\"recipe_id\":\"97\",\"quantity\":\"70\",\"quantity_verbal\":\"\",\"unity\":\"Ml\",\"name\":\"Olivenöl\",\"last_modified\":\"1613814153582\"},\n" +
                    "{\"id\":\"212\",\"recipe_id\":\"98\",\"quantity\":\"400\",\"quantity_verbal\":\"\",\"unity\":\"Gr\",\"name\":\"Mehl\",\"last_modified\":\"1621803399132\"},\n" +
                    "{\"id\":\"213\",\"recipe_id\":\"98\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Ei\",\"last_modified\":\"1621803406728\"},\n" +
                    "{\"id\":\"214\",\"recipe_id\":\"98\",\"quantity\":\"1\",\"quantity_verbal\":\"ca. 1\",\"unity\":\"Glas\",\"name\":\"Wasser, heiß\",\"last_modified\":\"1621803448518\"},\n" +
                    "{\"id\":\"215\",\"recipe_id\":\"98\",\"quantity\":\"1.5\",\"quantity_verbal\":\"\",\"unity\":\"Packung\",\"name\":\"Tomaten passiert \",\"last_modified\":\"1621803487787\"},\n" +
                    "{\"id\":\"216\",\"recipe_id\":\"98\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Zwiebel \",\"last_modified\":\"1621803496426\"},\n" +
                    "{\"id\":\"218\",\"recipe_id\":\"98\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Knoblauch\",\"last_modified\":\"1621803511447\"},\n" +
                    "{\"id\":\"219\",\"recipe_id\":\"98\",\"quantity\":\"300\",\"quantity_verbal\":\"\",\"unity\":\"Gr\",\"name\":\"Ricotta\",\"last_modified\":\"1621803651117\"},\n" +
                    "{\"id\":\"220\",\"recipe_id\":\"98\",\"quantity\":\"200\",\"quantity_verbal\":\"\",\"unity\":\"Gr\",\"name\":\"Spinat, Tiefkühl\",\"last_modified\":\"1621803569991\"},\n" +
                    "{\"id\":\"221\",\"recipe_id\":\"98\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Pistazien oder Pinienkerne\",\"last_modified\":\"1621803594258\"},\n" +
                    "{\"id\":\"222\",\"recipe_id\":\"98\",\"quantity\":\"100\",\"quantity_verbal\":\"\",\"unity\":\"Gr\",\"name\":\"Appenzeller \",\"last_modified\":\"1621803679450\"},\n" +
                    "{\"id\":\"223\",\"recipe_id\":\"3\",\"quantity\":\"500\",\"quantity_verbal\":\"\",\"unity\":\"Gr\",\"name\":\"Ricotta\",\"last_modified\":\"1640898337964\"},\n" +
                    "{\"id\":\"224\",\"recipe_id\":\"3\",\"quantity\":\"300\",\"quantity_verbal\":\"\",\"unity\":\"Gr\",\"name\":\"Spinat\",\"last_modified\":\"1640898353383\"},\n" +
                    "{\"id\":\"225\",\"recipe_id\":\"3\",\"quantity\":\"400\",\"quantity_verbal\":\"\",\"unity\":\"Gr\",\"name\":\"Mehl\",\"last_modified\":\"1640898368505\"},\n" +
                    "{\"id\":\"226\",\"recipe_id\":\"3\",\"quantity\":\"2\",\"quantity_verbal\":\"\",\"unity\":\"Stück\",\"name\":\"Tomaten Passiert\",\"last_modified\":\"1640898392379\"},\n" +
                    "{\"id\":\"227\",\"recipe_id\":\"3\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Ahornsirup\",\"last_modified\":\"1640898401838\"},\n" +
                    "{\"id\":\"228\",\"recipe_id\":\"3\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Pinienkerne\",\"last_modified\":\"1640898410690\"},\n" +
                    "{\"id\":\"229\",\"recipe_id\":\"3\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Ei\",\"last_modified\":\"1640898420251\"},\n" +
                    "{\"id\":\"230\",\"recipe_id\":\"3\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Wasser\",\"last_modified\":\"1640898424489\"},\n" +
                    "{\"id\":\"231\",\"recipe_id\":\"4\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Koriander gemahlen\",\"last_modified\":\"1642971031286\"},\n" +
                    "{\"id\":\"232\",\"recipe_id\":\"4\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Kurkuma\",\"last_modified\":\"1642971039680\"},\n" +
                    "{\"id\":\"233\",\"recipe_id\":\"4\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Kardamom\",\"last_modified\":\"1642971049449\"},\n" +
                    "{\"id\":\"234\",\"recipe_id\":\"99\",\"quantity\":\"4\",\"quantity_verbal\":\"1 pro Person \",\"unity\":\"\",\"name\":\"Paprika rot\",\"last_modified\":\"1644699680712\"},\n" +
                    "{\"id\":\"235\",\"recipe_id\":\"99\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Couscous\",\"last_modified\":\"1644699702591\"},\n" +
                    "{\"id\":\"236\",\"recipe_id\":\"99\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Feta\",\"last_modified\":\"1644699710121\"},\n" +
                    "{\"id\":\"237\",\"recipe_id\":\"99\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Blattspinat\",\"last_modified\":\"1644699742584\"},\n" +
                    "{\"id\":\"238\",\"recipe_id\":\"99\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Pinienkerne \",\"last_modified\":\"1644699750470\"},\n" +
                    "{\"id\":\"239\",\"recipe_id\":\"99\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Tomaten passiert \",\"last_modified\":\"1644699771630\"},\n" +
                    "{\"id\":\"240\",\"recipe_id\":\"99\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Zwiebel \",\"last_modified\":\"1644699778495\"},\n" +
                    "{\"id\":\"241\",\"recipe_id\":\"99\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Knoblauch \",\"last_modified\":\"1644699785730\"},\n" +
                    "{\"id\":\"242\",\"recipe_id\":\"99\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Zimt\",\"last_modified\":\"1644699791987\"},\n" +
                    "{\"id\":\"243\",\"recipe_id\":\"99\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Thymian \",\"last_modified\":\"1644699800260\"},\n" +
                    "{\"id\":\"244\",\"recipe_id\":\"99\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Chili\",\"last_modified\":\"1644700231226\"},\n" +
                    "{\"id\":\"245\",\"recipe_id\":\"100\",\"quantity\":\"300\",\"quantity_verbal\":\"\",\"unity\":\"Gramm\",\"name\":\"Hühnerbrust\",\"last_modified\":\"1646510502836\"},\n" +
                    "{\"id\":\"246\",\"recipe_id\":\"100\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"Bund\",\"name\":\"Frühlingszwiebel\",\"last_modified\":\"1646510317297\"},\n" +
                    "{\"id\":\"247\",\"recipe_id\":\"100\",\"quantity\":\"2\",\"quantity_verbal\":\"\",\"unity\":\"cm\",\"name\":\"Ingwer\",\"last_modified\":\"1646510345025\"},\n" +
                    "{\"id\":\"248\",\"recipe_id\":\"100\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"Liter\",\"name\":\"Hühnerbrühe\",\"last_modified\":\"1646510366498\"},\n" +
                    "{\"id\":\"249\",\"recipe_id\":\"100\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"Dose\",\"name\":\"Kokosmilch \",\"last_modified\":\"1646510388546\"},\n" +
                    "{\"id\":\"250\",\"recipe_id\":\"100\",\"quantity\":\"3\",\"quantity_verbal\":\"\",\"unity\":\"EL\",\"name\":\"Sojasauce\",\"last_modified\":\"1646510408756\"},\n" +
                    "{\"id\":\"251\",\"recipe_id\":\"100\",\"quantity\":\"2\",\"quantity_verbal\":\"\",\"unity\":\"TL\",\"name\":\"Currypaste\",\"last_modified\":\"1646510427969\"},\n" +
                    "{\"id\":\"252\",\"recipe_id\":\"100\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Paprikaschote\",\"last_modified\":\"1646510444041\"},\n" +
                    "{\"id\":\"253\",\"recipe_id\":\"100\",\"quantity\":\"100\",\"quantity_verbal\":\"\",\"unity\":\"Gramm\",\"name\":\"Champignons \",\"last_modified\":\"1646510509565\"},\n" +
                    "{\"id\":\"254\",\"recipe_id\":\"100\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"Stängel \",\"name\":\"Zitronengras\",\"last_modified\":\"1646510533684\"},\n" +
                    "{\"id\":\"255\",\"recipe_id\":\"100\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Chilischote\",\"last_modified\":\"1646510555309\"},\n" +
                    "{\"id\":\"256\",\"recipe_id\":\"100\",\"quantity\":\"125\",\"quantity_verbal\":\"\",\"unity\":\"Gramm \",\"name\":\"Chinesische Eiernudeln\",\"last_modified\":\"1646510583639\"},\n" +
                    "{\"id\":\"257\",\"recipe_id\":\"100\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"EL\",\"name\":\"Öl\",\"last_modified\":\"1646510597553\"},\n" +
                    "{\"id\":\"258\",\"recipe_id\":\"100\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Salz \",\"last_modified\":\"1646510613375\"},\n" +
                    "{\"id\":\"259\",\"recipe_id\":\"100\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Pfeffer\",\"last_modified\":\"1646510620218\"},\n" +
                    "{\"id\":\"260\",\"recipe_id\":\"26\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Mehl\",\"last_modified\":\"1648666808452\"},\n" +
                    "{\"id\":\"261\",\"recipe_id\":\"26\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Salz\",\"last_modified\":\"1648666814170\"},\n" +
                    "{\"id\":\"262\",\"recipe_id\":\"26\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Öl\",\"last_modified\":\"1648666818583\"},\n" +
                    "{\"id\":\"263\",\"recipe_id\":\"26\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Wasser\",\"last_modified\":\"1648666825201\"},\n" +
                    "{\"id\":\"264\",\"recipe_id\":\"26\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Creme Fraiche\",\"last_modified\":\"1648666835929\"},\n" +
                    "{\"id\":\"265\",\"recipe_id\":\"26\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Pesto\",\"last_modified\":\"1648666841460\"},\n" +
                    "{\"id\":\"266\",\"recipe_id\":\"26\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Tomaten\",\"last_modified\":\"1648666851109\"},\n" +
                    "{\"id\":\"267\",\"recipe_id\":\"26\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Mozzarella \",\"last_modified\":\"1648666857810\"},\n" +
                    "{\"id\":\"268\",\"recipe_id\":\"26\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Feta\",\"last_modified\":\"1648666862608\"},\n" +
                    "{\"id\":\"269\",\"recipe_id\":\"26\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Peperoni \",\"last_modified\":\"1648666871021\"},\n" +
                    "{\"id\":\"270\",\"recipe_id\":\"101\",\"quantity\":\"600\",\"quantity_verbal\":\"Ca. 150g pro Person \",\"unity\":\"Gramm\",\"name\":\"Sepia\",\"last_modified\":\"1666546715851\"},\n" +
                    "{\"id\":\"271\",\"recipe_id\":\"101\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Grüner Spargel\",\"last_modified\":\"1666546730557\"},\n" +
                    "{\"id\":\"272\",\"recipe_id\":\"101\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Zwiebel \",\"last_modified\":\"1666546740241\"},\n" +
                    "{\"id\":\"273\",\"recipe_id\":\"101\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Knoblauch \",\"last_modified\":\"1666546747910\"},\n" +
                    "{\"id\":\"274\",\"recipe_id\":\"101\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"Rispe\",\"name\":\"Kirschtomaten \",\"last_modified\":\"1666546764831\"},\n" +
                    "{\"id\":\"275\",\"recipe_id\":\"101\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Steinpilze\",\"last_modified\":\"1666546783304\"},\n" +
                    "{\"id\":\"276\",\"recipe_id\":\"102\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Rote Zwiebeln \",\"last_modified\":\"1667850357546\"},\n" +
                    "{\"id\":\"277\",\"recipe_id\":\"102\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Ahornsirup\",\"last_modified\":\"1667850368324\"},\n" +
                    "{\"id\":\"278\",\"recipe_id\":\"102\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Balsamico\",\"last_modified\":\"1667850380329\"},\n" +
                    "{\"id\":\"279\",\"recipe_id\":\"102\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Rosinen in Grappa\",\"last_modified\":\"1667850401570\"},\n" +
                    "{\"id\":\"280\",\"recipe_id\":\"102\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Pinienkerne geröstet \",\"last_modified\":\"1667850421418\"},\n" +
                    "{\"id\":\"281\",\"recipe_id\":\"102\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Chilli\",\"last_modified\":\"1667850429451\"},\n" +
                    "{\"id\":\"282\",\"recipe_id\":\"103\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Tomaten\",\"last_modified\":\"1668456421860\"},\n" +
                    "{\"id\":\"283\",\"recipe_id\":\"103\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Rote Zwiebeln\",\"last_modified\":\"1668456438327\"},\n" +
                    "{\"id\":\"284\",\"recipe_id\":\"103\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Knoblauch\",\"last_modified\":\"1668456456982\"},\n" +
                    "{\"id\":\"285\",\"recipe_id\":\"103\",\"quantity\":\"2\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Karotten\",\"last_modified\":\"1668456482888\"},\n" +
                    "{\"id\":\"286\",\"recipe_id\":\"103\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Brokkoli\",\"last_modified\":\"1668456492237\"},\n" +
                    "{\"id\":\"287\",\"recipe_id\":\"103\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Spinat\",\"last_modified\":\"1668456499705\"},\n" +
                    "{\"id\":\"288\",\"recipe_id\":\"103\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Rote Paprika\",\"last_modified\":\"1668456515662\"},\n" +
                    "{\"id\":\"289\",\"recipe_id\":\"103\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Champignons\",\"last_modified\":\"1668456527597\"},\n" +
                    "{\"id\":\"290\",\"recipe_id\":\"103\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Erbsen\",\"last_modified\":\"1668456549610\"},\n" +
                    "{\"id\":\"291\",\"recipe_id\":\"103\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Paella-Reis\",\"last_modified\":\"1668456565898\"},\n" +
                    "{\"id\":\"292\",\"recipe_id\":\"103\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Paprikapulver\",\"last_modified\":\"1668456586015\"},\n" +
                    "{\"id\":\"293\",\"recipe_id\":\"103\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Paprika geräuchert\",\"last_modified\":\"1668456599177\"},\n" +
                    "{\"id\":\"294\",\"recipe_id\":\"103\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Pfeffer\",\"last_modified\":\"1668456606384\"},\n" +
                    "{\"id\":\"295\",\"recipe_id\":\"103\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Kurkuma\",\"last_modified\":\"1668456613970\"},\n" +
                    "{\"id\":\"296\",\"recipe_id\":\"103\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Cheyenne-Pfeffer\",\"last_modified\":\"1668456628634\"},\n" +
                    "{\"id\":\"297\",\"recipe_id\":\"103\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Safran\",\"last_modified\":\"1668456637899\"},\n" +
                    "{\"id\":\"298\",\"recipe_id\":\"104\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Fenchel\",\"last_modified\":\"1676109561292\"},\n" +
                    "{\"id\":\"299\",\"recipe_id\":\"104\",\"quantity\":\"0.5\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Steckrübe\",\"last_modified\":\"1676109578254\"},\n" +
                    "{\"id\":\"300\",\"recipe_id\":\"104\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"EL\",\"name\":\"Creme Fraiche \",\"last_modified\":\"1676109602547\"},\n" +
                    "{\"id\":\"301\",\"recipe_id\":\"105\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Calamar\",\"last_modified\":\"1676109896490\"},\n" +
                    "{\"id\":\"302\",\"recipe_id\":\"105\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Thai-Spargel \",\"last_modified\":\"1676109907089\"},\n" +
                    "{\"id\":\"303\",\"recipe_id\":\"105\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Knoblauch \",\"last_modified\":\"1676109913247\"},\n" +
                    "{\"id\":\"304\",\"recipe_id\":\"105\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Kirschtomaten \",\"last_modified\":\"1676109918512\"},\n" +
                    "{\"id\":\"305\",\"recipe_id\":\"106\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Ei\",\"last_modified\":\"1693165647813\"},\n" +
                    "{\"id\":\"306\",\"recipe_id\":\"106\",\"quantity\":\"250\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Mehl\",\"last_modified\":\"1693165691291\"},\n" +
                    "{\"id\":\"307\",\"recipe_id\":\"106\",\"quantity\":\"160\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Margarine\",\"last_modified\":\"1693165686692\"},\n" +
                    "{\"id\":\"308\",\"recipe_id\":\"106\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Zucchini\",\"last_modified\":\"1693165711365\"},\n" +
                    "{\"id\":\"309\",\"recipe_id\":\"106\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Feta\",\"last_modified\":\"1693165719818\"},\n" +
                    "{\"id\":\"310\",\"recipe_id\":\"106\",\"quantity\":\"4\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Eier\",\"last_modified\":\"1693165729630\"},\n" +
                    "{\"id\":\"311\",\"recipe_id\":\"106\",\"quantity\":\"1\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Schmand\",\"last_modified\":\"1693165737910\"},\n" +
                    "{\"id\":\"312\",\"recipe_id\":\"106\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Thymian \",\"last_modified\":\"1693165746375\"},\n" +
                    "{\"id\":\"313\",\"recipe_id\":\"106\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Estragon \",\"last_modified\":\"1693165751899\"},\n" +
                    "{\"id\":\"314\",\"recipe_id\":\"109\",\"quantity\":\"30\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Koriandersamen\",\"last_modified\":\"1699254481824\"},\n" +
                    "{\"id\":\"315\",\"recipe_id\":\"109\",\"quantity\":\"15\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Kreuzkümmelsamen\",\"last_modified\":\"1699254510159\"},\n" +
                    "{\"id\":\"316\",\"recipe_id\":\"109\",\"quantity\":\"15\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Fenchelsamen\",\"last_modified\":\"1699254528098\"},\n" +
                    "{\"id\":\"317\",\"recipe_id\":\"109\",\"quantity\":\"15\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Schwarzer Pfeffer\",\"last_modified\":\"1699254553062\"},\n" +
                    "{\"id\":\"318\",\"recipe_id\":\"109\",\"quantity\":\"2\",\"quantity_verbal\":\"\",\"unity\":\"El\",\"name\":\"Pflanzenöl\",\"last_modified\":\"1699254578996\"},\n" +
                    "{\"id\":\"319\",\"recipe_id\":\"109\",\"quantity\":\"8\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Curryblätter\",\"last_modified\":\"1699254598363\"},\n" +
                    "{\"id\":\"320\",\"recipe_id\":\"109\",\"quantity\":\"8\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Curryblätter\",\"last_modified\":\"1699254600480\"},\n" +
                    "{\"id\":\"321\",\"recipe_id\":\"109\",\"quantity\":\"70\",\"quantity_verbal\":\"\",\"unity\":\"gr\",\"name\":\"Chilischoten mittelscharf, getro\",\"last_modified\":\"1699254649238\"},\n" +
                    "{\"id\":\"322\",\"recipe_id\":\"109\",\"quantity\":\"0.25\",\"quantity_verbal\":\"\",\"unity\":\"Tl\",\"name\":\"Kurkuma\",\"last_modified\":\"1699254682765\"},\n" +
                    "{\"id\":\"323\",\"recipe_id\":\"110\",\"quantity\":\"900\",\"quantity_verbal\":\"\",\"unity\":\"Gr\",\"name\":\"Dorade\",\"last_modified\":\"1699731279286\"},\n" +
                    "{\"id\":\"324\",\"recipe_id\":\"112\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Hähnchenbrust\",\"last_modified\":\"1704748140239\"},\n" +
                    "{\"id\":\"325\",\"recipe_id\":\"112\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Zwiebeln\",\"last_modified\":\"1704748152186\"},\n" +
                    "{\"id\":\"326\",\"recipe_id\":\"112\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Paprika\",\"last_modified\":\"1704748158522\"},\n" +
                    "{\"id\":\"327\",\"recipe_id\":\"112\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Saure Gurken\",\"last_modified\":\"1704748168119\"},\n" +
                    "{\"id\":\"328\",\"recipe_id\":\"112\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Schmand\",\"last_modified\":\"1704748175188\"},\n" +
                    "{\"id\":\"330\",\"recipe_id\":\"112\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Tomatenmark\",\"last_modified\":\"1704748194360\"},\n" +
                    "{\"id\":\"331\",\"recipe_id\":\"112\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Paprikapulver\",\"last_modified\":\"1704748222011\"},\n" +
                    "{\"id\":\"332\",\"recipe_id\":\"112\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Pfeffer\",\"last_modified\":\"1704748228820\"},\n" +
                    "{\"id\":\"333\",\"recipe_id\":\"112\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Gemüsebrühe\",\"last_modified\":\"1704748237545\"},\n" +
                    "{\"id\":\"334\",\"recipe_id\":\"112\",\"quantity\":\"0\",\"quantity_verbal\":\"\",\"unity\":\"\",\"name\":\"Stärkemehl\",\"last_modified\":\"1704749308875\"}\n" +
                    "]"
        )
    }
}