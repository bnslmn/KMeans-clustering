# KMeans-clustering

KNN Algorithm

Given a set of points & a positive integer K > 0 , we return K lists of points (clusters) according to the classical K-Means Algorithm

The initialization step which is done is called KNN ++ 

The code is improved with two accelerations proposed in the paper :

Phillips S.J. (2002) Acceleration of K-Means and Related Clustering Algorithms. In: Mount D.M.,
Stein C. (eds) Algorithm Engineering and Experiments. ALENEX 2002.
Lecture Notes in Computer Science, vol 2409. Springer, Berlin, Heidelberg. https://doi.org/10.1007/3-540-45643-0_13



# to execute 

Mark `run` in the target of `build.xml` & simply execute the ant

# experimentation 

Given this set of points :

![kMean](https://user-images.githubusercontent.com/77028316/141666302-d63139ff-15f4-4b8e-811a-ecc0b2cfd196.png)

We obtain : 

![kMean_after](https://user-images.githubusercontent.com/77028316/141666321-b8034d07-61b9-42b6-98ac-e4a524679b62.png)

And after a (very harsh) benchmark of tests :

![kMeans_afterall](https://user-images.githubusercontent.com/77028316/141666334-60829357-f019-42df-a664-0fe3b5dc0558.png)




