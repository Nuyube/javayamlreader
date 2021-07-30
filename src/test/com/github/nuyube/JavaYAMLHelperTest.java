package com.github.nuyube;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import com.github.nuyube.javayamlreader.InvalidYamlException;
import com.github.nuyube.javayamlreader.JavaYAMLHelper;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class JavaYAMLHelperTest 
{
    public final String yaml_1 = "#comment0\nblock1_1:\n  #comment1_1\n  key1_1: value1_1\n  block2_1:\n    #comment2_1\n    key2_1: value2_1\n    key2_2: value2_2\n    #comment2_2\n    key2_3: value2_3\n  block2_2:\n    key3_1: value3_1\n    #comment3_1\n    key3_2: value3_2";
  
    @Test
    public void testCommentRemovalSimple()
    {
        String yaml = yaml_1;
        String withoutComments = JavaYAMLHelper.removeAllComments(yaml);
        assertTrue(!withoutComments.contains("#"));
    }
    @Test
    public void testDenameBlock() {
        String yaml = yaml_1;
        yaml = JavaYAMLHelper.removeAllComments(yaml);
        String withoutName = JavaYAMLHelper.denameBlock(yaml);
        assertTrue(!withoutName.startsWith("block1_1"));
    }
    @Test
    public void testGetIndent() {
        String yaml = yaml_1;
        yaml = JavaYAMLHelper.removeAllComments(yaml);
        String withoutName = JavaYAMLHelper.denameBlock(yaml);
        int indent = JavaYAMLHelper.getIndent(withoutName);
        assertEquals(indent, 2);
    }
    @Test
    public void testGetBlocks() {
        String yaml = yaml_1;
        yaml = JavaYAMLHelper.removeAllComments(yaml);
        String withoutName = JavaYAMLHelper.denameBlock(yaml);
        System.out.println("Without name:\n"+withoutName);
        String[] blocks = JavaYAMLHelper.getBlocks(withoutName);
        int ctr = 0;
        for (String string : blocks) {
            System.out.println("Block " + ++ctr  +"\n"+ string );
        }
        assertEquals(3, blocks.length);
    }

   /* @Test
    public void testGetBlock() {
        String yaml = yaml_1;
        yaml = JavaYAMLHelper.removeAllComments(yaml);
        String withoutName = JavaYAMLHelper.denameBlock(yaml);
        String block2 = JavaYAMLHelper.getBlock("block2_1", withoutName);
        String block3 = JavaYAMLHelper.getBlock("block2_2", withoutName);

        try{
        block2 = JavaYAMLHelper.unindentBlock(block2);
        block3 = JavaYAMLHelper.unindentBlock(block3);
        }
        catch (InvalidYamlException e) {
            System.out.println(e);
            assertTrue(false);
        }
        assertTrue(block2.startsWith("block2_1"));
        assertTrue(block3.startsWith("block2_2"));

    }*/
    @Test
    public void testGetKey() {
        String yaml = yaml_1;
        yaml = JavaYAMLHelper.removeAllComments(yaml);
        String withoutName = JavaYAMLHelper.denameBlock(yaml);
        String block2 = JavaYAMLHelper.getBlock("block2_1", withoutName);
        String block3 = JavaYAMLHelper.getBlock("block2_2", withoutName);

        try{
        block2 = JavaYAMLHelper.unindentBlock(block2);
        block3 = JavaYAMLHelper.unindentBlock(block3);
        }
        catch (InvalidYamlException e) {
            System.out.println(e);
            assertTrue(false);
        }
        assertTrue(block2.startsWith("block2_1"));
        assertTrue(block3.startsWith("block2_2"));

        String key1 = JavaYAMLHelper.getValueFromKey("key2_1", block2);
        String key2 = JavaYAMLHelper.getValueFromKey("key3_1", block3);
        assertTrue (key1.equals("value2_1"));
        assertTrue (key2.equals("value3_1"));

    }
}
