package cn.edu.pku.recruitment.resumeProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cn.edu.pku.recruitment.classifier.Classifier;
import cn.edu.pku.recruitment.knowledgeBase.KnowledgeBase;

/**
 * 简历信息
 * @author lzh@pku
 * @version 1.0
 * */
public class ResumeInfo {
	
	/** 简历包含的技能名称 */
	public ArrayList <String> skillName = new ArrayList <String> ();
	/** 技能向量 */
	public int [] skillVector = new int [KnowledgeBase.skillNumberM];
	
	/** 初始化该类型的对象 */
	public ResumeInfo ()
	{
		for(int i = 0; i < KnowledgeBase.skillNumberM; i ++)
			skillVector[i] = 0;
	}

	/** 
	 * 提取简历信息
	 * @param resumePath 简历文件路径
	 * @throws IOException 找不到简历文件
	 * */
	public void process(String resumePath) throws IOException
	{
		InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(resumePath)), "UTF-8");
		BufferedReader reader = new BufferedReader(isr);
		String line = null;
		while((line = reader.readLine()) != null)
		{
			String [] tokens = line.trim().split(" +");
			for(int i = 0; i < tokens.length; i ++)
			{
				tokens[i] = tokens[i].replace("\n", "");
				tokens[i] = tokens[i].replace("\r", "");
				tokens[i] = tokens[i].replace("\t", "");
				tokens[i] = tokens[i].trim();
				
				int index = KnowledgeBase.isSkill(tokens[i]);
				if(index != -1)
					skillVector[index] ++;
			}
		}
		for(int i = 0; i < skillVector.length; i ++)
			if(skillVector[i] > 0)
				skillName.add(new String(KnowledgeBase.skillList[i]));
			
		reader.close();
	}
	
	public static void main(String args[]) throws IOException
	{
		KnowledgeBase.loadKnowledgeBase();
		Classifier.loadModel();
		Classifier classifier = new Classifier ();
		double [] score;
		
		ResumeInfo resumeInfo1 = new ResumeInfo();
		resumeInfo1.process("E:\\workj2ee\\RS\\CVs\\pro_1_19_933182959x.txt");
		for(int i = 0; i < resumeInfo1.skillVector.length; i ++)
			if(resumeInfo1.skillVector[i] > 0)
				System.out.print(resumeInfo1.skillVector[i] + " " + KnowledgeBase.skillList[i] + "\n");
//		System.out.println();
//		for(int i = 0; i < resumeInfo1.skillName.size(); i ++)
//			System.out.print(resumeInfo1.skillName.get(i) + " ");
		System.out.println();
		score = classifier.getDistri(resumeInfo1.skillVector);
		for(int i = 0; i < score.length; i ++)
			System.out.println(KnowledgeBase.positionList[i] + "	" + score[i]);
		System.out.println();
		
		ResumeInfo resumeInfo2 = new ResumeInfo();
		resumeInfo2.process("E:\\workj2ee\\RS\\CVs\\pro_1_29_931541535x.txt");
		for(int i = 0; i < resumeInfo2.skillVector.length; i ++)
			if(resumeInfo2.skillVector[i] > 0)
				System.out.print(resumeInfo2.skillVector[i] + " " + KnowledgeBase.skillList[i] + "\n");
//		System.out.println();
//		for(int i = 0; i < resumeInfo2.skillName.size(); i ++)
//			System.out.print(resumeInfo2.skillName.get(i) + " ");
		System.out.println();
		score = classifier.getDistri(resumeInfo2.skillVector);
		for(int i = 0; i < score.length; i ++)
			System.out.println(KnowledgeBase.positionList[i] + "	" + score[i]);
		System.out.println();
	}
}
