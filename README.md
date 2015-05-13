# mamn01-project
Project for the Advanced Interaction Design (MAMN01) at LTH

## Notes for the report

* Infoga testprotokoll, hur gick det till när användarna testade? Varför? Kan vi lära något utav det?
* Lägga till use cases
* Hur valde vi sensorerna och momenten? 
* Vad trodde vi att användarna skulle säga och reagera? Vad tydde de på? Analys
* vilka tekniker användes? Brainstorming! Bland annat 
* Väljer flera interaktionselement för att förstärka kommunikationen med användaren

## Weeklog
### Week 1
This week we started by presenting the idea for our application for the rest of the group.
We also finished our low fi prototype. 

![alt tag](https://raw.github.com/dabbe/mamn01-project/master/images/lowfi.jpg)

We decided on using 5 screens for the application. The inital screen which only has a start button. We also discussed having a level picker at this screen, but this wont be implemented unless we feel we have time left in the end of the project. The second screen consist of the search-for-the-treasure-part of the game, which we decided will be a compass. The screen will also show the over all progress, in a bar. This is where the player collects their total points. 

The two next screens are the digging and blowing screens, which are very similar and gives the user two very different way of interacting with the game. The last screen will consist of a quiz. We decided this quiz will be in the form of a multiple choice test, which the user has to pass to get the treasure.

### Week 2
We distributed the responsibility of each screen to one member of the group: 
* Daniel had the navigation screen.
* Oscar had the digging screen.
* Jacob had the blowing screen.
* Christian had the Quiz screen.
 
We have come far and we are a bit puzzled by how far we have come. Every screen has its core functionality working. (reading sensors). It was decided that the navigation would use the Fused Location API from Android to get the best possible location data. We have not implemented any distance indicators but we are going to. The question is really, what kind of feedback gives the best understanding of the situation? Haptic, colors, sound?.

The digging screen uses the gyroscope to look for changes in tilting of the device. It has not been decided exactly how the movement will be but we have tried a few which all works. 
 
### Användartest 26/4 (inte färdigutvecklad prototyp):
8 åring (tillhör toppskitet i klassen) - Tyckte idéen var intressant och provade alla moment utom compass (fick inte att fungera). 
Grävning - inte helt lätt att förstå först hur man skulle göra, och visste inte när man lyckats (skulle behövas bättre feedback). Men när man sa "använd telefonen som en spade", var det rätt intuitivt.
Blåsning - Började först blåsa på skärmen, men efter att berätta att man skulle blåsa undertill på telefonen så gick det bra. (Behövs någon typ av feedback, då man inte ser skärmen så lätt när man blåser)
Quiz - Tog liten stund att läsa, men gick bra. De 3 vanliga talen var lätt/lagom (lär sig multiplication i skolan idag i hans ålder). Ljudfeedback var lite roligt tyckte testpersonen. Men förstod inte * tecknet, förstod inte att det var multiplikation.

10 åring (tillhör toppskitet i klassen) - Gav samma feedback som 8 åring, hade dock enklare att läsa dialogskärmen på quizen. 
De ser fram emot att prova en mer fungerande app :)

Med lite instruktioner innan varje aktivitet kan nog göra det lättare, och lite mer ljudfeedback (många spel de annars spelar bygger mest på ljudfeedback)

### Möte 5/5
* Jacob: Bilder, blåsa bort sand effekt.
* Oscar: Bild från Jacob, snygga till designen, checkbox på startskärmen.
* Christian: Fler frågor och i olika nivåer, (nivå 1: math1 osv.), kategorier (math, geo, historia)
* Daniel: Fixa färdigt kompasskärmen med dialog för hints samt loadingskärm medan location hämtas.

### Möte 12/5
Vi har gjort ett testprotokoll som finns nedskrivet på papper
Use case:
* Start igong ett spel
* Navigera till en skatt
* Gräva upp skatten 
* Blås bort sanden
* Lös problem för att låsa upp skatten

### Möte 13/5
* bättre färger på kompassen 
* bättre vibration 
* 
Danne: Fixa wakeLock, resume problem efter hittat första skatten, ok-knapp dialogruta, fixa dialogruta, finish ruta, byt till gul bakgrund, 
Jacob: ok-knapp dialogruta, finish ruta, öka mängd sand att blåsa, 
Oscar: ok-knapp dialogruta, finish ruta, gula bakgrund startskärm och bild med vägen, 
Christian: ok-knapp dialogruta, finish ruta med ok, onbackpressed()
