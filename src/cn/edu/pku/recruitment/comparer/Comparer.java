package cn.edu.pku.recruitment.comparer;

import java.io.IOException;
import java.util.ArrayList;

import cn.edu.pku.recruitment.classifier.Classifier;
import cn.edu.pku.recruitment.knowledgeBase.KnowledgeBase;
import cn.edu.pku.recruitment.positionProcessor.PositionInfo;
import cn.edu.pku.recruitment.resumeProcessor.ResumeInfo;

/**
 * 职位与简历比较器
 * @author lzh@pku
 * @version 1.0
 * */
public class Comparer {

	/** 向量相对熵 */
	public static final int M = 0;
	/** 组合概率分布相对熵 */
	public static final int P = 1;
	/** 相对熵指示器 */
	public static int id = 1;
	/** 权重系数 */
	public static double alpha = 0.45;
	
	/** 
	 * 职位与简历比较
	 * @param pInfo PositionInfo类型对象
	 * @param rInfo ResumeInfo类型对象
	 * @return 职位与简历的匹配度
	 * */
	public double compare(PositionInfo pInfo, ResumeInfo rInfo)
	{
		//positionInfo[KnowledgeBase.skillNumber]存储的是职位编号
		if(pInfo.cateTag == -1)
			pInfo.cateTag = KnowledgeBase.positionNumberM;
		double score = 0.0, SumScore = 0.0;
		
		ArrayList <String> unmatchedSkillName = new ArrayList <String> ();
		
		//匹配分值计算
		for(int i = 0; i < KnowledgeBase.skillNumberM; i ++)
		{
			if(pInfo.skillVector[i] > 0)
				SumScore = SumScore + KnowledgeBase.matchProb[pInfo.cateTag][i];
			//这是能够匹配的部分
			if(rInfo.skillVector[i] > 0 && pInfo.skillVector[i] > 0)
			{
				double ratio = (double) rInfo.skillVector[i] / (double) pInfo.skillVector[i];
				if(ratio > 1.0)
					ratio = 1.0;
				score = score + ratio * KnowledgeBase.matchProb[pInfo.cateTag][i];
			}
			//这是不能匹配的部分
			else if(pInfo.skillVector[i] > 0)
				unmatchedSkillName.add(new String(KnowledgeBase.skillList[i]));
		}
		double matchScore = score;
		System.out.println("直接匹配 " + matchScore);
		
		//近似分值计算
		if(rInfo.skillName.size() > 0)
		{
			for(int i = 0; i < unmatchedSkillName.size(); i ++)
			{
				int rIndex = KnowledgeBase.isSkill(unmatchedSkillName.get(i));
				int oIndex = KnowledgeBase.skillNumberM - 1;
				double similarity = 0.0;
				for(int j = 0; j < rInfo.skillVector.length; j ++)
				{
					if(rInfo.skillVector[j] > 0 && KnowledgeBase.skillSimilarity[rIndex][j] > similarity)
					{
						oIndex = j;
						similarity = KnowledgeBase.skillSimilarity[rIndex][j];
					}
				}
				SumScore = SumScore + KnowledgeBase.matchProb[pInfo.cateTag][rIndex];
				double ratio = (double) rInfo.skillVector[oIndex] / (double) pInfo.skillVector[rIndex];
				if(ratio > 1.0)
					ratio = 1.0;
				score = score + ratio * KnowledgeBase.matchProb[pInfo.cateTag][rIndex] * similarity;
			}
		}
		System.out.println("相关匹配 " + (score - matchScore));
		System.out.println("最大可能得分 " + SumScore);
		
		double diff = 0.0;
		if(id == 0)
		{
			diff = Math.pow(relativeEntropy(pInfo.skillVector, rInfo.skillVector), 1.0);
		}
		else if(id == 1)
		{
			Classifier classifier = new Classifier ();
			
			double [] resumeP = classifier.getDistri(rInfo.skillVector);
//			for(int i = 0; i < resumeP.length; i ++)
//				System.out.print(resumeP[i] + "	");
//			System.out.println();
			double [] positionP = classifier.getDistri(pInfo.skillVector);
//			for(int i = 0; i < positionP.length; i ++)
//				System.out.print(positionP[i] + "	");
//			System.out.println();
			diff = Math.pow(relativeEntropyP(positionP, resumeP), 1.0);
			System.out.println("KL散度均值 " + diff);
		}
		
		if(SumScore != 0)
		{
			//System.err.println("Info：" + score / SumScore);
			score = score / SumScore * alpha + (1.0 - diff) * (1.0 - alpha);
		}
		else
		{
			//System.err.println("Info：" + score);
			score = score * alpha + (1.0 - diff) * (1.0 - alpha);	
		}
		System.out.println("计算加权匹配结果" + score);
		return score;
	}
	
	/**
	 * 技能向量相对熵
	 * @param pVector 职位技能向量
	 * @param rVector 简历技能向量
	 * @return 技能向量相对熵
	 * */
	public double relativeEntropy(int [] pVector, int [] rVector)
	{
		double [] pVectorD = new double[pVector.length];
		double [] rVectorD = new double[rVector.length];
		double pSum = 0.0, rSum = 0.0;
		for(int i = 0; i < pVector.length; i ++)
			if((double) (pVector[i]) > pSum)
				pSum = pSum + (double) (pVector[i]);
		for(int i = 0; i < rVector.length; i ++)
			if((double) (rVector[i]) > rSum)
				rSum = rSum + (double) (rVector[i]);
		for(int i = 0; i < pVector.length; i ++)
			pVectorD[i]	= (double) (pVector[i]) / pSum;
		for(int i = 0; i < rVector.length; i ++)
			rVectorD[i]	= (double) (rVector[i]) / rSum;
		return relativeEntropyP(pVectorD, rVectorD);
	}
	
	/**
	 * 技能组合概率分布相对熵
	 * @param pVector 职位技能向量
	 * @param rVector 简历技能向量
	 * @return 技能组合概率分布相对熵
	 * */
	public double relativeEntropyP(double [] positionP, double [] resumeP)
	{
		double rpD = Math.log10(4.0);
		for(int i = 0; i < resumeP.length; i ++)
		{
			if(positionP[i] > 0 && resumeP[i] > 0)
				rpD = rpD
					+ resumeP[i] * Math.log(resumeP[i] / (positionP[i] + resumeP[i]))
					+ positionP[i] * Math.log(positionP[i] / (positionP[i] + resumeP[i]));
		}
				
		rpD = Math.exp(rpD) / (1.0 + Math.exp(rpD));
		return rpD;
	}
	
	public static void main(String args[]) throws IOException
	{
		KnowledgeBase.loadKnowledgeBase();
		Classifier.loadModel();
		
		PositionInfo positionInfo2 = new PositionInfo();
		positionInfo2.process("E:\\workj2ee\\RS\\CVs\\pro_1_19_933182959x\\1_34_196911813251.txt");
		for(int i = 0; i < positionInfo2.skillVector.length; i ++)
			System.out.print(positionInfo2.skillVector[i] + " ");
		System.out.println();
		for(int i = 0; i < positionInfo2.skillName.size(); i ++)
			System.out.print(positionInfo2.skillName.get(i) + " ");
		System.out.println();
		
		ResumeInfo resumeInfo2 = new ResumeInfo();
		resumeInfo2.process("E:\\workj2ee\\RS\\CVs\\pro_1_29_931541535x.txt");
		for(int i = 0; i < resumeInfo2.skillVector.length; i ++)
			System.out.print(resumeInfo2.skillVector[i] + " ");
		System.out.println();
		for(int i = 0; i < resumeInfo2.skillName.size(); i ++)
			System.out.print(resumeInfo2.skillName.get(i) + " ");
		System.out.println();
		
		Comparer comparer = new Comparer();
		System.out.println(comparer.compare(positionInfo2, resumeInfo2));
	}
}
