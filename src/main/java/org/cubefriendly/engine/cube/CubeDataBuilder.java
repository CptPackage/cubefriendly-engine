package org.cubefriendly.engine.cube;

import com.google.common.collect.Lists;
import org.mapdb.*;

import java.util.List;

/**
 * Cubefriendly
 * Created by david on 27.02.15.
 */
public class CubeDataBuilder {
    private final DB db;
    private BTreeMap<int[],String[]> data;
    private List<Integer> sizes;

    public CubeDataBuilder(DB db) {
        this.db = db;
        if(db.exists("__data__")){
            this.data = db.treeMap("__data__");
            this.sizes = (List<Integer>)db.<String,Object>hashMap("__metadata__").get("__sizes__");
        }else{
            this.data = db.<int[],String>treeMapCreate("__data__").comparator(Fun.INT_ARRAY_COMPARATOR).make();
            this.sizes = Lists.newArrayList();
        }
    }

    public CubeDataBuilder add(List<Integer> dimensions,List<String> metrics) {
        int[] key = new int[dimensions.size()];
        if(sizes == null || sizes.isEmpty()){
            sizes = Lists.newArrayList(dimensions);
        }
        for(int i = 0; i < key.length ; i++){
            key[i] = dimensions.get(i);
            sizes.add(i,Math.max(sizes.get(i),dimensions.get(i)));
            sizes.remove(i + 1);
        }
        data.put(key, metrics.toArray(new String[metrics.size()]));
        return this;
    }

    public CubeData build() {
        db.hashMapCreate("__metadata__").makeOrGet().put("__sizes__",sizes);
        return new CubeData(data,sizes, db);
    }
}
