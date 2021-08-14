#JobQueue
---
###Version 1 - Ye Basic Job Queue<br />
A simple example of a job queue using a ThreadPool and the Runnable 
interface.  Does not return any values.
---
###Version 2 - Callable and Future<br />
Building on version 1, implements the Callable and Future interface to execute 
background threads and retrieve results. <br />
Notable changes:

- Shifted from Runnable to Callable&lt;T&gt;
- Added a Job Interface to provide a place for Future<T> to reside (T is set to String for now)

---
