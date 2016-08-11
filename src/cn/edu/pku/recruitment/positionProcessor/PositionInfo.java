package cn.edu.pku.recruitment.positionProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cn.edu.pku.recruitment.knowledgeBase.KnowledgeBase;

/**
 * 职位信息
 * @author lzh@pku
 * @version 1.0
 * */
public class PositionInfo {

	/** 职位要求的技能名称 */
	public ArrayList <String> skillName = new ArrayList <String> ();
	/** 技能向量 */
	public int [] skillVector = new int [KnowledgeBase.skillNumberM];
	/** 类别标记 */
	public int cateTag = -1;
	
	/** 初始化该类型的对象 */
	public PositionInfo ()
	{
		for(int i = 0; i < KnowledgeBase.skillNumberM; i ++)
			skillVector[i] = 0;
	}
	
	/** 
	 * 提取职位信息
	 * @param positionPath 职位文件路径
	 * @throws IOException 找不到职位文件
	 * */
	public void process(String positionPath) throws IOException
	{
		//System.out.println(new File(positionPath).getAbsolutePath());
		InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(positionPath)));
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
				
				int index = KnowledgeBase.isSkill(tokens[i]);
				if(index != -1)
					skillVector[index] ++;
			}
		}
		int maxSkillNumberIndex = -1;//出现最多次数的技能下标
		int maxSkillNumber = -1;
		for(int i = 0; i < skillVector.length - 1; i ++)
		{
			if(skillVector[i] > 0)
			{
				skillName.add(new String(KnowledgeBase.skillList[i]));
				if(skillVector[i] > maxSkillNumber)
				{
					maxSkillNumber = skillVector[i];
					maxSkillNumberIndex = i;
				}
			}
		}
		int positionIndex = -1;
		if(maxSkillNumberIndex != -1) {
			for(int i = 0; i < KnowledgeBase.positionNumberM; i ++)
			{
				if(KnowledgeBase.positionList[i].equals(KnowledgeBase.skillList[maxSkillNumberIndex]))
				{
					positionIndex = i;
					break;
				}
			}
		}
//		skillVector[skillVector.length - 1] = positionIndex;
		cateTag = positionIndex;
		
		reader.close();
	}

	/** 
	 * 技能特征是否显著
	 * @param threshold 技能数量下限
	 * */
	public boolean isSkillCatched(int threshold) {
		for(int i = 0; i < skillVector.length; i ++)
			threshold -= skillVector[i];
		return threshold < 0;
	}
	
	/** 
	 * 设置职位类别
	 * @param c 职位类别标记
	 * */
	public void setPositionCategory(int c)
	{
		cateTag = c;
		return;
	}
	
	public static void main(String args[]) throws IOException
	{
		KnowledgeBase.loadKnowledgeBase();
		
		PositionInfo positionInfo1 = new PositionInfo();
		positionInfo1.process("E:\\workj2ee\\RS\\CVs\\pro_1_19_933182959x\\1_33_333045011251.txt");
		for(int i = 0; i < positionInfo1.skillVector.length; i ++)
			System.out.print(positionInfo1.skillVector[i] + " ");
		System.out.println();
		for(int i = 0; i < positionInfo1.skillName.size(); i ++)
			System.out.print(positionInfo1.skillName.get(i) + " ");
		System.out.println();
		
		PositionInfo positionInfo2 = new PositionInfo();
		positionInfo2.process("E:\\workj2ee\\RS\\CVs\\pro_1_19_933182959x\\1_34_196911813251.txt");
		for(int i = 0; i < positionInfo2.skillVector.length; i ++)
			System.out.print(positionInfo2.skillVector[i] + " ");
		System.out.println();
		for(int i = 0; i < positionInfo2.skillName.size(); i ++)
			System.out.print(positionInfo2.skillName.get(i) + " ");
		System.out.println();
	}
}
