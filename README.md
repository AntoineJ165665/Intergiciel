#TP Intergiciel Programmation par Composants

Auteur : Antoine Jamin | Mail : antoine.jamin@uphf.fr | N°téléphone : 06 64 50 77 71 | N°étudiant : 22 10 33 25

Ce projet est une application SheelCLient qui utilise Kafka, SpringBoot, LibreTranlate, une database POstgreSQL. IL s'agit d'un système de messagerie. UN client s'y connecte, voit quels autre clients sont connectés et peut leur envoyer des messages privés. Ces messages passent par les topics Kafka, qui vont servir à les stocker dans la base de données, et les traduire avec LibreTranslate. Le message traduit est lui aussi stocké. Après que le message a été traduit, il est renvoyé au topicIN et est envoyé à son destinataire, traduit.

Les parties individuelles de mon code marchent. La fonction de traduction fonctionne, les fonctions d'écriture et de lecture en base de données fonctionnent, les outils sont bien sous docker et correctement paramétrés. Cependant je n'ai pas réussi à faire fonctionner les messages. Je n'ai pas réussi à corriger ce problème à temps, et donc le Projet est incomplet.
