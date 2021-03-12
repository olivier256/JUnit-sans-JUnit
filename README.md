# JUnit-sans-JUnit
On crée une classe qui hérite de AbstractTest (et dont le nom se termine par "Test"),

cette classe contient des méthodes dont le nom commence par "test",

il suffit ensuite de lancer la méthode héritée test() de cette classe, et toutes les méthodes de test se lanceront.

Si on veut aller plus loin,

on fait hériter toutes ces classes de l'interface Test,

et on lancer la méthode RunAllTests.main.
