# Overview

## Vision

1. To become **the biggest student project in the world**.<br>
   Biggest = many contributors, many users, relatively large code base (150k-200k LoC), evolving over a long period.<br>
   "Biggest" above also implies an exceptionally high quality standard because high quality is a necessity for the long-term survival of a big student project.

1. To become **a model and a training ground for Software Engineering students** who want to learn SE skills in the context of a non-trivial real software product.

## Challenges

The project differs from typical student projects in the following areas, which makes it more challenging and the experience more enriching.
+ **Developers**: All developers are novices and their involvement with the project is short term and part time.
+ **Code**: Working with legacy code written by past developers is harder than writing from scratch.
+ **Data**: The data in the live system are confidential and developers are not allowed to see them. This makes troubleshooting harder.
+ **Releases**: Frequent releases to an active user database requires us to maintain "production quality" constantly.
+ **Platform**: TEAMMATES is running on Google App Engine cloud platform, which adds the following challenges.
  - It is an emerging platform evolving rapidly. We have to keep up.
  - It imposes various restrictions on the application, e.g. each request to the app has to be served within 60 seconds.
  - It charges us based on usage. We have to optimize usage.
+ **Software Engineering**: As TEAMMATES serves as a model system for training students, it should also focus on applying good SE techniques.

## Principles

We apply these principles to meet the challenges stated above.
+ **We keep moving forward, always**: We release frequently, in weekly [time-boxed iterations](http://en.wikipedia.org/wiki/Timeboxing). Every week, our product becomes better than the previous week. This means "go back and rewrite from scratch" is only a last resort.
+ **We are agile**: We are able to change the system quickly and easily to match emerging requirements. We aim for **minimal yet sufficient documentation**.
+ **We defend our code with tests, fiercely**: Since we practice [collective code ownership](http://www.extremeprogramming.org/rules/collective.html), we have to make sure the code is not accidentally broken by others. We use fully automated regression testing. The testing automation level of this project is probably higher than 99% of the projects out there.
+ **We are "Gods" of the few tools we use**: We stick to a minimal toolset. Adding third-party tools and libraries to the project is done only if there is a STRONG justification. Only mature, stable, and well-supported tools are considered. Once selected, we should know the tool very well to get the best out of it.
+ **We value quality more than functionality**: Our job is not to "anyhow get it to work". The system should be good enough to be considered a "model" software. Our code should be of "exceptionally" high quality: all code is reviewed before accepting.
+ **We know what we are doing**: For us, it is not enough to know something is broken, we should also know why it is broken. It is not enough to get something working, we should know how we got it to work.
+ **We seek the best, not stop at the first**: We do not settle for the first workable method to solve a problem. We study other ways of solving it and decide what is the best way for us.
+ **We take pains to save the team from pain**: Whenever we had to spend effort in figuring out something, we refine code/documents so that others don't have to go through the same pain. Whenever we figured out a less painful way of doing something, we make sure everyone in the team learn it too.
