from django import forms
from django.forms import ModelForm

from epic.core.models import Item
from epic.datarequests.models import DataRequest


class DataRequestForm(ModelForm):
    name = forms.CharField(max_length=Item.MAX_ITEM_NAME_LENGTH, widget=forms.TextInput(attrs={'size':42 , 'onfocus': "MPClearField(this)"}))
    description = forms.CharField(max_length=Item.MAX_ITEM_DESCRIPTION_LENGTH, widget=forms.Textarea(attrs={'rows':6, 'cols':42, 'onfocus': "MPClearField(this)"}))
    tags = forms.CharField(max_length=Item.MAX_ITEM_TAGS_LENGTH, required=False, widget=forms.TextInput(attrs={'size':42 , 'onfocus': "MPClearField(this)"}))
        
    class Meta:
        model = DataRequest
        exclude = \
            ['creator', 'rendered_description', 'status', 'slug', 'is_active']
