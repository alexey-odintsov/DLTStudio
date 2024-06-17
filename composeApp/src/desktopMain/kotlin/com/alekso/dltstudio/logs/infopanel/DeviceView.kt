package com.alekso.dltstudio.logs.infopanel

import androidx.compose.ui.geometry.Rect

data class DeviceView(
    val rect: Rect,
    val id: String? = null,
) {
    companion object {
        /**
         *     @Override
         *     public String toString() {
         *         StringBuilder out = new StringBuilder(256);
         *         out.append(getClass().getName());
         *         out.append('{');
         *         out.append(Integer.toHexString(System.identityHashCode(this)));
         *         out.append(' ');
         *         switch (mViewFlags&VISIBILITY_MASK) {
         *             case VISIBLE: out.append('V'); break;
         *             case INVISIBLE: out.append('I'); break;
         *             case GONE: out.append('G'); break;
         *             default: out.append('.'); break;
         *         }
         *         out.append((mViewFlags & FOCUSABLE) == FOCUSABLE ? 'F' : '.');
         *         out.append((mViewFlags&ENABLED_MASK) == ENABLED ? 'E' : '.');
         *         out.append((mViewFlags&DRAW_MASK) == WILL_NOT_DRAW ? '.' : 'D');
         *         out.append((mViewFlags&SCROLLBARS_HORIZONTAL) != 0 ? 'H' : '.');
         *         out.append((mViewFlags&SCROLLBARS_VERTICAL) != 0 ? 'V' : '.');
         *         out.append((mViewFlags&CLICKABLE) != 0 ? 'C' : '.');
         *         out.append((mViewFlags&LONG_CLICKABLE) != 0 ? 'L' : '.');
         *         out.append((mViewFlags&CONTEXT_CLICKABLE) != 0 ? 'X' : '.');
         *         out.append(' ');
         *         out.append((mPrivateFlags&PFLAG_IS_ROOT_NAMESPACE) != 0 ? 'R' : '.');
         *         out.append((mPrivateFlags&PFLAG_FOCUSED) != 0 ? 'F' : '.');
         *         out.append((mPrivateFlags&PFLAG_SELECTED) != 0 ? 'S' : '.');
         *         if ((mPrivateFlags&PFLAG_PREPRESSED) != 0) {
         *             out.append('p');
         *         } else {
         *             out.append((mPrivateFlags&PFLAG_PRESSED) != 0 ? 'P' : '.');
         *         }
         *         out.append((mPrivateFlags&PFLAG_HOVERED) != 0 ? 'H' : '.');
         *         out.append((mPrivateFlags&PFLAG_ACTIVATED) != 0 ? 'A' : '.');
         *         out.append((mPrivateFlags&PFLAG_INVALIDATED) != 0 ? 'I' : '.');
         *         out.append((mPrivateFlags&PFLAG_DIRTY_MASK) != 0 ? 'D' : '.');
         *         out.append(' ');
         *         out.append(mLeft);
         *         out.append(',');
         *         out.append(mTop);
         *         out.append('-');
         *         out.append(mRight);
         *         out.append(',');
         *         out.append(mBottom);
         *         final int id = getId();
         *         if (id != NO_ID) {
         *             out.append(" #");
         *             out.append(Integer.toHexString(id));
         *             final Resources r = mResources;
         *             if (id > 0 && Resources.resourceHasPackage(id) && r != null) {
         *                 try {
         *                     String pkgname;
         *                     switch (id&0xff000000) {
         *                         case 0x7f000000:
         *                             pkgname="app";
         *                             break;
         *                         case 0x01000000:
         *                             pkgname="android";
         *                             break;
         *                         default:
         *                             pkgname = r.getResourcePackageName(id);
         *                             break;
         *                     }
         *                     String typename = r.getResourceTypeName(id);
         *                     String entryname = r.getResourceEntryName(id);
         *                     out.append(" ");
         *                     out.append(pkgname);
         *                     out.append(":");
         *                     out.append(typename);
         *                     out.append("/");
         *                     out.append(entryname);
         *                 } catch (Resources.NotFoundException e) {
         *                 }
         *             }
         *         }
         *         if (mAutofillId != null) {
         *             out.append(" aid="); out.append(mAutofillId);
         *         }
         *         out.append("}");
         *         return out.toString();
         *     }
         */
        fun parse(text: String): List<DeviceView>? {
            val list = mutableListOf<DeviceView>()
            val matches = Regex("""
                \{(?<id2>[a-z0-9]+)?\s?(?<flags1>[A-Z\.]+)?\s?(?<flags2>[A-Z\.]+)?\s?(?<left>\d+),(?<top>\d+)-(?<right>\d+),(?<down>\d+)\s?(?<id>#[a-f0-9]+)?\s?(?<resid>[a-zA-Z0-9\-\_]+:id/[a-zA-Z0-9\_\-]+)?\s?(?<aid>aid\=[0-9]+)?\}
            """.trimIndent()).findAll(text)
            for (match in matches) {
                println(match.value)
                list.add(
                    DeviceView(
                        Rect(
                            match.groups["left"]!!.value.toFloat(),
                            match.groups["top"]!!.value.toFloat(),
                            match.groups["right"]!!.value.toFloat(),
                            match.groups["down"]!!.value.toFloat()
                        ),
                        id = match.groups["resid"]?.value
                    )
                )
            }
            return list
        }

    }
}