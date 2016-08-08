## LevelSetCBEL - Levelset with texture analysis
  
### Introduction

<p> LevelSetCBEL is an ImageJ plugin written in Java 1.7 and contains two methods:
* Modeled-based levelset proposed by Audrey H. Zhuang and Daniel J. Valentino (2005), Laboratory of Neuro Imaging (LONI) and Division of Interventional Neuro Radiology
* Texture-based levelset based on Modeled-based levelset proposed by Laboratory of Computational Biomedical Engineering (2016).
LevelSetCBEL is tested by Internet Brain Segmentation Repository (IBSR) and LONI
Image Data Archive (IDA) in MINC format. Libraries used in this work includes Java Swing
for GUI, BioFormats for medical image I/O and iText 7 for result report.
<p> **What is skullstripping ?** Skull-stripping, belonging to one of the preprocessing step in neuroimaging analysis, aim to remove the non-brain tissues and leave the entire brain region.
<p> **What is Levelset method ?** The level set approach allows the evolving front to change topology, break, and merge, which means that the evolving front can extract the boundaries of particularly intricate contours. <https://math.berkeley.edu/~sethian/2006/Applications/Medical_Imaging/artery.html>

### Software snapshots
![Alt text](https://raw.githubusercontent.com/blueclowd/Skullstripping/a0df17e583b8cc1fbb0e18e06450a3c2d0daaadc/LevelSetCBEL.png)
![Alt text](https://raw.githubusercontent.com/blueclowd/Skullstripping/master/Illustration%202.png)

