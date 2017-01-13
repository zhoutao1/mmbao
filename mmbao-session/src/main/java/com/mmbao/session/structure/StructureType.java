package com.mmbao.session.structure;

/**
 * Created by gongbin on 2016/11/4.
 */
public enum StructureType {
    MapStructure(new StructureHandle() {
        @Override
        public ISessionStructure instance() {
            return new MapStructure();
        }
    });

    private StructureHandle handle;

    StructureType(StructureHandle handle) {
        this.handle = handle;
    }

    public ISessionStructure instance()
    {
        return handle.instance();
    }

    interface StructureHandle { ISessionStructure instance();}
}
