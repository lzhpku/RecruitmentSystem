package cn.edu.pku.recruitment.test;

import java.io.IOException;

import cn.edu.pku.recruitment.classifier.Classifier;
import cn.edu.pku.recruitment.comparer.Comparer;
import cn.edu.pku.recruitment.knowledgeBase.KnowledgeBase;
import cn.edu.pku.recruitment.positionProcessor.PositionInfo;
import cn.edu.pku.recruitment.resumeProcessor.ResumeInfo;

public class Test {

	public static void main(String args[]) throws IOException
	{
		KnowledgeBase.setPositionFile(100, "positionList top100.txt");
		KnowledgeBase.setSkillFile(100, "skillList top100.txt");
		KnowledgeBase.setProbFile("contribution matrix.txt");
		KnowledgeBase.setSimilFile("similarity matrix.txt");
		KnowledgeBase.loadKnowledgeBase();
		
		Classifier.setProbFile(15, 40, "classifier param.txt");
		Classifier.loadModel();
		Classifier classifier = new Classifier ();
		
		double [] distribution;
		
		/**
		 * 职位信息中间结果
		 * */
		System.out.println("职位文件分析中间结果*************************");
		PositionInfo positionInfo = new PositionInfo();
		positionInfo.process("Positions\\pro_1_29_931541535x\\5_12_120478912250.txt");
		for(int i = 0; i < positionInfo.skillVector.length - 1; i ++)
			if(positionInfo.skillVector[i] > 0)
				System.out.print(KnowledgeBase.skillList[i] + " " + positionInfo.skillVector[i] + "\n");
		System.out.println();
		distribution = classifier.getDistri(positionInfo.skillVector);
		for(int i = 0; i < distribution.length; i ++)
			System.out.println(KnowledgeBase.positionList[i] + "	" + distribution[i]);
		System.out.println();
		
		/**
		 * 简历信息中间结果
		 * */
		System.out.println("简历文件分析中间结果*************************");
		ResumeInfo resumeInfo = new ResumeInfo();
		resumeInfo.process("Resumes\\pro_1_29_931541535x.txt");
		for(int i = 0; i < resumeInfo.skillVector.length; i ++)
			if(resumeInfo.skillVector[i] > 0)
				System.out.print(KnowledgeBase.skillList[i] + " " + resumeInfo.skillVector[i] + "\n");
		System.out.println();
		distribution = classifier.getDistri(resumeInfo.skillVector);
		for(int i = 0; i < distribution.length; i ++)
			System.out.println(KnowledgeBase.positionList[i] + "	" + distribution[i]);
		System.out.println();
		
		/**
		 * 职位与简历匹配度计算
		 * */
		System.out.println("职位与简历匹配度计算*************************");
		Comparer comparer = new Comparer();
		System.out.println(comparer.compare(positionInfo, resumeInfo));
	}
}