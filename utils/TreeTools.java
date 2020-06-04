package com.yidao.court.prelitigation.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author duwei
 */
public class TreeTools {
    
    /**
     * @return
     * idChar
     * pidChar
     * childList
     */
    @SuppressWarnings("unchecked")
    public static <T> List<Map<String, Object>> getTree(List<T> list, String idChar, String pidChar) throws Exception {
        Map<Object,Object> allMap=new HashMap<>();
        List<Map<String,Object>> allList=new ArrayList<>();
        for (T node : list) {
            Map<String,Object> map= BeanTool.objectToMap(node);
            allMap.put( map.get( idChar ), map);
            allList.add(map);
        }
        List<Map<String,Object>> treeList=new ArrayList<>();
        
        for (Map<String,Object> node : allList) {
            boolean isTop=true;

            Map<String,Object> pNode=(Map<String,Object>)allMap.get( node.get( pidChar ) );
            if( pNode!=null){
                List<Map<String,Object>> childList=(List<Map<String, Object>>)pNode.get( "childList");
                if(childList!=null){
                    childList.add(node );
                }else{
                    childList=new ArrayList<>();
                    childList.add(  node );
                    pNode.put( "childList",childList );
                }
                isTop=false;
            }

            if(isTop){
                treeList.add( node );
            }
        }
        return treeList;
    }


}
