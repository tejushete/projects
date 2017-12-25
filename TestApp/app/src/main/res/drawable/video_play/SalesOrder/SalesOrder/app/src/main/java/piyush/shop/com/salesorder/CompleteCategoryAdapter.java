package piyush.shop.com.salesorder;

import piyush.shop.com.salesorder.SOUtility;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hp on 22-Jul-17.
 */
public class CompleteCategoryAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    List<String> mCategoryList;
    HashMap<String, List<String>> mSubCategoryMap;
    public ProductActivity productActivityContext;

    CompleteCategoryAdapter(Context context) {
        mContext = context;
    }

    private void showSubCategoryDialog(final int groupPos, final CompleteCategoryAdapter adapter, final String defaultVal) {
        Log.d("TAG", "on click");

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.sub_category_input_view);

        final EditText etCategory = (EditText) dialog.findViewById(R.id.etCategory);
        etCategory.setText(defaultVal);

        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etCategory.getText().toString().isEmpty() == true) {
                    etCategory.setError("Please enter valid sub category Name");
                    return;
                }

                SOUtility.dbHandler.addSubCategory(etCategory.getText().toString().trim(), adapter.mCategoryList.get(groupPos));
                List<String> list = adapter.mSubCategoryMap.get(mCategoryList.get(groupPos));

                if (list == null) {
                    list = new ArrayList<String>();
                }

                if (list.contains(etCategory.getText().toString().trim()) == true) {
                    Toast toast = Toast.makeText(mContext, "Sub Category Already Added", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

                if (defaultVal.isEmpty() == false) {
                    SOUtility.dbHandler.removeSubCategory(mCategoryList.get(groupPos), defaultVal);
                    SOUtility.dbHandler.updateSubCategoryInProducts(defaultVal, etCategory.getText().toString());
                    list.remove(defaultVal);
                }

                list.add(etCategory.getText().toString());
                adapter.mSubCategoryMap.put(adapter.mCategoryList.get(groupPos), list);
                productActivityContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public int getGroupCount() {
        return mCategoryList != null ? mCategoryList.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<String> list = (mSubCategoryMap.get(mCategoryList.get(groupPosition)));
        return ((list != null) ? list.size() : 0);
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void showDeleteMainCategoryDialog(final int groupPos, final String category) {
        Log.d("TAG", "on click");

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.popup_ok_cancel);

        Button btnDialogOK = (Button) dialog.findViewById(R.id.btnDialogOK);
        Button btnDialogCancel = (Button) dialog.findViewById(R.id.btnDialogCancel);

        final TextView tvDialogContent = (TextView) dialog.findViewById(R.id.tvDialogContent);
        tvDialogContent.setText(tvDialogContent.getText() + " " + category + " Category?");

        btnDialogOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String category = mCategoryList.get(groupPos);
                        mCategoryList.remove(category);
                        mSubCategoryMap.remove(category);
                        SOUtility.dbHandler.removeCategory(category);
                        SOUtility.dbHandler.removeSubCategories(category);

                        SOUtility.dbHandler.removeCategoryInProducts(category);

                        productActivityContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                    }
                }).start();
            }
        });

        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showMainCategoryEditInputDialog(final int position, final String category) {
        Log.d("TAG", "on click");

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.main_category_input_view);

        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        final EditText etCategory = (EditText) dialog.findViewById(R.id.etCategory);
        etCategory.setText(category);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        final String etCategoryVal = etCategory.getText().toString().trim();
                        if (etCategoryVal.isEmpty() == true) {
                            etCategory.setError("Enter valid category Name");
                            return;
                        }

                        if (true == mCategoryList.contains(etCategoryVal)) {
                            productActivityContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast toast = Toast.makeText(mContext, etCategoryVal + " already exists.", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            return;
                        }

                        List<String> list = mSubCategoryMap.get(category);

                        mSubCategoryMap.remove(category);
                        mCategoryList.remove(category);
                        mCategoryList.add(etCategoryVal);

                        mSubCategoryMap.put(etCategoryVal, list);

                        SOUtility.dbHandler.removeCategory(category);
                        SOUtility.dbHandler.removeSubCategories(category);
                        for (String sub : list) {
                            SOUtility.dbHandler.addSubCategory(sub, etCategoryVal);
                        }
                        SOUtility.dbHandler.addCategory(etCategoryVal);

                        SOUtility.dbHandler.updateCategoryInProducts(category, etCategoryVal);

                        productActivityContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });

                    }
                }).start();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View group_row = convertView;

        if (group_row == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            group_row = inflater.inflate(R.layout.category_view, parent, false);
        }

        TextView tvMainCategory = (TextView) group_row.findViewById(R.id.tvMainCategory);
        tvMainCategory.setText(mCategoryList.get(groupPosition));

        ImageView ivAddSubCategory = (ImageView) group_row.findViewById(R.id.ivAddSubCategory);
        ivAddSubCategory.setTag(groupPosition);
        ivAddSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                showSubCategoryDialog(position, CompleteCategoryAdapter.this, "");
            }
        });

        ImageView ivEditMainCategory = (ImageView) group_row.findViewById(R.id.ivEditMainCategory);
        ivEditMainCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMainCategoryEditInputDialog(groupPosition, mCategoryList.get(groupPosition));
            }
        });

        ImageView ivRemoveMainCategory = (ImageView) group_row.findViewById(R.id.ivRemoveMainCategory);
        ivRemoveMainCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteMainCategoryDialog(groupPosition, mCategoryList.get(groupPosition));
            }
        });

        if (mCategoryList.get(groupPosition).equalsIgnoreCase("Other") == true) {
            ivEditMainCategory.setVisibility(View.GONE);
            ivRemoveMainCategory.setVisibility(View.GONE);
            ivAddSubCategory.setVisibility(View.GONE);
        } else {
            ivEditMainCategory.setVisibility(View.VISIBLE);
            ivRemoveMainCategory.setVisibility(View.VISIBLE);
            ivAddSubCategory.setVisibility(View.VISIBLE);
        }

        return group_row;
    }

    private void showDeleteSubCategoryDialog(final int groupPos, final int childPos, final String subCategory) {
        Log.d("TAG", "on click");

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.popup_ok_cancel);

        Button btnDialogOK = (Button) dialog.findViewById(R.id.btnDialogOK);
        Button btnDialogCancel = (Button) dialog.findViewById(R.id.btnDialogCancel);

        final TextView tvDialogContent = (TextView) dialog.findViewById(R.id.tvDialogContent);
        tvDialogContent.setText(tvDialogContent.getText() + " " + subCategory + " subCategory?");

        btnDialogOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String category = mCategoryList.get(groupPos);

                        List<String> list = mSubCategoryMap.get(category);
                        mSubCategoryMap.remove(category);
                        list.remove(subCategory);
                        mSubCategoryMap.put(category, list);

                        SOUtility.dbHandler.removeSubCategory(category, subCategory);
                        SOUtility.dbHandler.removeSubCategoryInProducts(subCategory);

                        productActivityContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                    }
                }).start();
            }
        });

        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View child_row = convertView;

        if (child_row == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            child_row = inflater.inflate(R.layout.sub_category, parent, false);
        }

        TextView tvSubCategory = (TextView) child_row.findViewById(R.id.tvSubCategory);
        final List<String> list = mSubCategoryMap.get(mCategoryList.get(groupPosition));
        tvSubCategory.setText(list.get(childPosition));

        ImageView ivEditSubCategory = (ImageView) child_row.findViewById(R.id.ivEditSubCategory);
        ivEditSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubCategoryDialog(groupPosition, CompleteCategoryAdapter.this, list.get(childPosition));
            }
        });

        ImageView ivRemoveSubCategory = (ImageView) child_row.findViewById(R.id.ivRemoveSubCategory);
        ivRemoveSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteSubCategoryDialog(groupPosition, childPosition, list.get(childPosition));
            }
        });

        if (list.get(childPosition).equalsIgnoreCase("Other") == true) {
            ivEditSubCategory.setVisibility(View.GONE);
            ivRemoveSubCategory.setVisibility(View.GONE);
        } else {
            ivEditSubCategory.setVisibility(View.VISIBLE);
            ivRemoveSubCategory.setVisibility(View.VISIBLE);
        }

        return child_row;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}