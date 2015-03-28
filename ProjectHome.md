# The Ernest project #

## Content ##

This Java project contains the source code of Ernest "the self-motivated agent". It also contains a framework to demonstrate Ernest's behavior in a simple environment called the [Small Loop Environment](http://e-ernest.blogspot.fr/2012/05/challenge-emergent-cognition.html). Please see the [blog](http://e-ernest.blogspot.com/) for demos and discussions. The complete explanation of the Ernest algorithm is available [here](http://e-ernest.blogspot.fr/2011/01/intrinsically-motivated-schema.html).

## Instructions ##

### Download the Ernest Project in Eclipse ###

Download the project in [Eclipse](http://www.eclipse.org/) using a version-control plugin such as [Subclipse](http://subclipse.tigris.org/) . The repository url is [https://e-ernest.googlecode.com/svn/trunk/e-ernest](https://e-ernest.googlecode.com/svn). Download revision [r296](https://code.google.com/p/e-ernest/source/detail?r=296) that was fully tested, unless otherwise instructed.

This project uses the following Java libraries (configured in buildpath/libraries) that may not be installed by default on your system:

- j3d (you can download it from http://java3d.java.net/binary-builds.html)

### Run the demo ###

Run the [Main.java](http://e-ernest.googlecode.com/svn/trunk/e-ernest/doc/ernest/Main.html) class. Watch Ernest's behavior in the Eclipse console. The Small Loop Environment is represented by ASCII characters as shown below (Ernest is represented by an arrowhead: v,>,^, or <, indicating its orientation).
```
xxxxxx
x   vx
x xx x
x  x x
xx   x
xxxxxx
```

Insert a breakpoint in the main loop to follow Ernest step by step, as instructed in the Main method of the Main.java class.

### Implement an Ernest agent in your simulated environment, or an Ernest controller in your robot ###

You can use the [Main.java](http://e-ernest.googlecode.com/svn/trunk/e-ernest/doc/ernest/Main.html) class as an example to use the Ernest algorithm.
Use the [Ernest.java](http://e-ernest.googlecode.com/svn/trunk/e-ernest/doc/ernest/Ernest.html) class to instantiate an Ernest agent:
```
IErnest ernest = new Ernest();
```
Use the [setParameters](http://e-ernest.googlecode.com/svn/trunk/e-ernest/doc/ernest/Ernest.html#setParameters%28int,%20int%29) method to set Ernest's parameters. Example that works in most cases:
```
ernest.setParameters(6, 10);
```
Use the [addInteraction](http://e-ernest.googlecode.com/svn/trunk/e-ernest/doc/ernest/Ernest.html#addInteraction%28java.lang.String,%20java.lang.String,%20int%29) method to specify the primitive interactions that are available to your Ernest agent and their values (primitive "actions" and "feedbacks" can be represented by the ascii strings of your choice). Example:
```
// "-": action "try to touch in front".
// ">": action "try to move forward".
// "f": feedback "false".
// "t": feedback "true".
ernest.addInteraction("-", "f", -1);  // Touch empty cell ahead - small effort.
ernest.addInteraction("-", "t", -1);  // Touch wall ahead - small effort.
ernest.addInteraction(">", "f", -10); // Bump into wall - strong dislike.
ernest.addInteraction(">", "t", 5);   // Move forward - enjoy.
```
Use the [Effect.java](http://e-ernest.googlecode.com/svn/trunk/e-ernest/doc/ernest/Effect.html) class to specify the "feedback" that the environment provides to Ernest on each interaction cycle:
```
IEffect effect_of_previous_action = new Effect();
```
On each interaction cycle, set the effect label. Example:
```
effect_of_previous_action.setLabel("t");
```
On each interaction cycle, use the [step](http://e-ernest.googlecode.com/svn/trunk/e-ernest/doc/ernest/Ernest.html#step%28ernest.IEffect%29) method to run Ernest one step. Example:
```
String next_action = ernest.step(effect_of_previous_action);
```
Implementing what `next_action` does is up to you.


Remember that Ernest [r296](https://code.google.com/p/e-ernest/source/detail?r=296) only learns sequential regularities of interaction (and exploits no other attribute of the class `effect` than the label). We are still working on a version that can learn spatio-sequential regularities.