package cn.edu.pku.recruitment.knowledgeBase;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 基本数据文件
 * @author lzh@pku
 * @version 1.0
 * */
public class KnowledgeBase {

	/** 职位数量 */
	public static int positionNumberM = 100;
	/** 技能数量 */
	public static int skillNumberM = 100;
	/** 职位列表文件路径 */
	public static String positionListFilePath = "positionList top100.txt";
	/** 职位列表 */
	public static String [] positionList;
	/** 技能列表文件路径 */
	public static String skillListFilePath = "skillList top100.txt";
	/** 技能列表 */
	public static String [] skillList;
	/** 匹配分值文件路径 */
	public static String matchProbFilePath = "contribution matrix.txt";
	/** 匹配分值矩阵 */
	public static double [][] matchProb;
	/** 相似分值文件路径 */
	public static String skillSimilarityFilePath = "similarity matrix.txt";
	/** 相似分值矩阵 */
	public static double [][] skillSimilarity;
	
	/** 
	 * 设置职位列表文件参数 
	 * @param _positionNumberM 职位数量
	 * @param _positionListFilePath 职位列表文件路径
	 * */
	public static void setPositionFile(int _positionNumberM, String _positionListFilePath)
	{
		positionNumberM = _positionNumberM;
		positionListFilePath = _positionListFilePath;
	}
	
	/** 
	 * 设置技能列表文件参数 
	 * @param _skillNumberM 技能数量
	 * @param _skillListFilePath 技能列表文件路径
	 * */
	public static void setSkillFile(int _skillNumberM, String _skillListFilePath)
	{
		skillNumberM = _skillNumberM;
		skillListFilePath = _skillListFilePath;
	}
	
	/** 
	 * 设置匹配分值文件参数 
	 * @param _matchProbFilePath 匹配分值文件路径
	 * */
	public static void setProbFile(String _matchProbFilePath)
	{
		matchProbFilePath = _matchProbFilePath;
	}
	
	/** 
	 * 设置相似分值文件参数 
	 * @param _skillSimilarityFilePath 相似分值文件路径
	 * */
	public static void setSimilFile(String _skillSimilarityFilePath)
	{
		skillSimilarityFilePath = _skillSimilarityFilePath;
	}
	
	/** 
	 * 加载职位列表，技能列表，匹配分值矩阵，相似分值矩阵
	 * @throws IOException 找不到相关文件
	 * */
	public static void loadKnowledgeBase() throws IOException
	{
		positionList = null;
		positionList = new String [positionNumberM];
		InputStreamReader isr = new InputStreamReader(new FileInputStream(positionListFilePath));
		BufferedReader reader = new BufferedReader(isr);
		String line = null;
		int counter = 0;
		while((line = reader.readLine()) != null && counter < positionNumberM)
		{
			String [] temp = line.trim().split("	");
			positionList[counter] = new String(temp[0]);
			counter ++;
		}
//		for(int i = 0; i < positionNumberM; i ++)
//			System.out.println(positionList[i]);
//		System.out.println("职位列表");
		
		skillList = null;
		skillList = new String [skillNumberM];
		isr = new InputStreamReader(new FileInputStream(skillListFilePath));
		reader = new BufferedReader(isr);
		line = null;
		counter = 0;
		while((line = reader.readLine()) != null && counter < skillNumberM)
			skillList[counter ++] = new String(line.trim());
//		for(int i = 0; i < skillNumberM; i ++)
//			System.out.println(skillList[i]);
//		System.out.println("技能列表");
		
		matchProb = null;
		matchProb = new double [positionNumberM + 1][skillNumberM];
		isr = new InputStreamReader(new FileInputStream(matchProbFilePath));
		reader = new BufferedReader(isr);
		line = null;
		counter = 0;
		while((line = reader.readLine()) != null && counter < positionNumberM + 1)
		{
			String [] temp = line.trim().split("	");
			for(int i = 0; i < skillNumberM; i ++)
				matchProb[counter][i] = Double.parseDouble(temp[i]);
			counter ++;
		}
//		for(int i = 0; i < positionNumberM; i ++)
//		{
//			for(int j = 0; j < skillNumberM; j ++)
//				System.out.print(matchProb[i][j] + "	");
//			System.out.println();
//		}
//		System.out.println("匹配概率矩阵");
		
		skillSimilarity = null;
		skillSimilarity = new double [skillNumberM + 1][skillNumberM + 1];
		isr = new InputStreamReader(new FileInputStream(skillSimilarityFilePath));
		reader = new BufferedReader(isr);
		line = null;
		counter = 0;
		while((line = reader.readLine()) != null && counter < skillNumberM)
		{
			line = line.trim().replace(" ", "");
			String [] temp = line.trim().split("	");
			for(int i = 0; i < skillNumberM; i ++)
				skillSimilarity[counter][i] = Double.parseDouble(temp[i]);
			counter ++;
		}
		for(int i = 0; i < skillNumberM + 1; i ++)
		{
			skillSimilarity[skillNumberM][i] = 0.0;
			skillSimilarity[i][skillNumberM] = 0.0;
		}
//		for(int i = 0; i < skillNumberM + 1; i ++)
//		{
//			for(int j = 0; j < skillNumberM + 1; j ++)
//				System.out.print(skillSimilarity[i][j] + "	");
//			System.out.println("**********这是第"+(i+1)+"行***********");
//		}
//		System.out.println("技能相似度矩阵");

		reader.close();
	}

	/** 
	 * 技能名称判断
	 * @param token 待识别的词素
	 * @return 技能标记，如果未找到对应的技能，则返回-1
	 * */
	public static int isSkill(String token)
	{
		for(int i = 0; i < skillList.length; i ++)
			if(skillList[i].equals(token))
				return i;
		return -1;
	}
	
	 /** 技能名称判断
	 * @param token 待识别的词素
	 * @return 技能标记，如果未找到对应的技能，则返回-1
	 * */
	public static int containsSkill(String str)
	{
		for(int i = 0; i < skillList.length; i ++)
			if(str.contains(skillList[i]))
				return i;
		return -1;
	}

}
