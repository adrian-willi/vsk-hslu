# Game Analyse
Aktuell hat das Game keine Log-Funktionen eingebaut. Nach Absprache in der Gruppe wurde entschieden, dass alle Klassen grob nach einem Schema erweitert werden, um schlussendlich ein möglichst einheitliches Logging zu erreichen.
Weiter ist gegeben, dass *weiter unten im Code* auch mehr Logging zu erwarten ist.

## Klassenspezifisch
### AppletFrame
Programmfenster mit kleiner Menüleiste. Enthält das eigentliche Spiel (StandaloneGameOfLife). Überscheibt die Methoden ProcessEvent (Close), showAboutDialog, showManualDialog, showLicenceDialog.
### Cell
Das Netz besteht aus vielen Zellen. Aus dieser Klasse werden die Zellobjekte erzeugt. Referenz über Hash, vermerkt zusätzlich Nachbar - was den nächsten Status definiert.
### CellGrid - Interface
Interface zwischen dem Canvas und GameOfLife. Damit wird Canvas generisch und kann unabhängig für andere Spiele genutzt werden (für ein Zellennetz).
### CellGridCanvas
Erweitert Canvas und stellt das Netz dar. Kommuniziert über das Interface. Enthält Bufferimages um flackern zu vermeiden. Stellt Methoden bereit um Grösse anzupassen.
### GameOfLifeGrid
Enthält das Zellennetz, die aktuelle Zeichnung und den Algorithmus um die Zeichnung anzupassen. Verwendet Hashtables um aktuelles und nächstes Shape speichern.
### StandaloneGameOfLife
Diese Klasse ist Einstiegspunkt der in Applikation (pom.xml: jar.start.class) und beinhaltet den Aufbau der grafischen Oberfläche. Teil davon ist auch die Veränderung des GUI (Kachelauflösung und generelle Grösse des Fensters)
### GameOfLifeControlsListener - Interface
Erweitert das Java Basis-Interface EventListener. Spezifiert die Events, welche durch die Controls ausgelöst werden. Beispielsweise wenn der Start/Stopp Button gedrückt wird.
### GameOfLifeControlsEvent
Eventklasse für die Klasse GameOfLifeControls. Objekte von dieser Klasse werden in der Klasse GameOfLifeControls generiert. 
### GameOfLifeControls
Die Klasse beinhaltet die GUI-controls für das Game of Life. Sie beinhaltet die Controls wie Shape, Zoom, Speed und Start/Stopp. Kommuniziert via GameOfLifeControlsListener.
### GameOfLife
Dies ist das Herz des Spiels. Es initialisiert die einzelnen Elemente und führt alles zusammen. Das Spiel wird darin gestartet, gestoppt, die Formen aufgerufen usw. In dieser Klasse werden parallele Threads aufgerufen. 
### Shape
Mit dieser Klasse können die Spielformen definiert werden. Sie enthält den Namen der Form sowie die Form selber als mehrdimensionales Array. 
### ShapeCollection
Die Klassen beinhaltet alle vordefinierten Formen des Spiels. Die Formen können darin abgerufen werden. 
### ShapeException
Die Klasse erbt von Exception. Diese Klasse definiert Exceptions in Zusammenhang mit den Formen des Spiels.
## Utility Klassen
### AboutDialog.java / AlertBox.java
Beide Klassen machen was der Name schon sagt. Hier wird, wenn über haupt, nur sehr wenig geloggt werden.
### EasyFile.java
Filehandler vom GUI aus verwendet. Kann bestehenden Formen speichern und zu laden. Das Exceptionhandling wird jedoch in StandaloneGameOfLife.java gemacht.
### ImageComponent.java
Wird verwendet um Bilder anzuzeigen (auch animated gifs) -> InterruptedException = Log.Error 
### LineEnumerator.java
Speichert einen Input String und gibt auf anfrage die einzelnen Lines zurück. Höchstens Problematisch bei sehr grossen Files. -> wenig bis kein Logging
### package-info.java
Kein Logging :-)
### TextFileDialog.java
Gibt den Inhalt eines Textfiles in einer Box aus. -> FileIO = Exception = Logging


## Log- Vorgaben/Schema
Da die Schnittstellenspezifikationen der Loggingkomponenten noch nicht bekannt sind, beschränken wir uns vorerst auf ein reduziertes Volumen an Log Levels.

- **INFO:** User-zentrisch, heisst auch verständlich für den Operator  oder regelmässige Systemoperationen.
- **DEBUG:** Wird da eingesetzt wo man natürlicherweise auch einen Breakpoint setzen würde.
Beispielsweise Game Start, spezielle Events während dem Spiel, Game Ende.
- ***TRACE:** Vorerst nicht verwendet.*
- **WARNING:** Abnormales Verhalten welches jedoch nicht zum Terminieren der Applikation führt. Bsp. Fehlerhafter Login Versuch oder konkret ein Verbindungsverlust zum Logserver 
- **ERROR:** An Stellen wo die Applikation erwarteterweise Terminiert. z.B. In den Catch-Blöcken für Ressourcen.
- ***FATAL:** Vorerst nicht verwendet.*
