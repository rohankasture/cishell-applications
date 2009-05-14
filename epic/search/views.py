import urllib

from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User, UserManager
from django.core.urlresolvers import reverse
from django.http import HttpResponseRedirect, HttpResponse
from django.shortcuts import render_to_response, get_object_or_404, get_list_or_404
from django.template import RequestContext, Context, loader
from django.utils import simplejson

from epic.core.models import Item

SERVELET_URL = 'http://localhost:8182/'

def search(request):
    # TODO:  TEST THIS!  Test 1 result and test many results
    responseData = {}
    error_message = None
    results = None
    
    SEARCH_PARAM = 'search_string'
    search_string = None
    
    if SEARCH_PARAM in request.POST:
        search_string = request.POST[SEARCH_PARAM]
    elif SEARCH_PARAM in request.GET:
        search_string = request.GET[SEARCH_PARAM]
      
    if search_string:
        try:
            # The java needs spaces to be +s
            search_string = search_string.replace(" ", "+")
            
# Uncomment to use servlet            
#            raw_data = urllib.urlopen(SERVELET_URL + 
#                                      '?search_string=' + 
#                                      search_string)

            raw_data = urllib.urlopen('http://epic.slis.indiana.edu/fake.json')
            json_object = simplejson.loads(raw_data.read())
            
            if 'result' in json_object:
                results = []
                for result in json_object['result']:
                    item_id = result['item_id']
                    try:
                        item = Item.objects.get(pk=item_id)
                        results.append({'item':item, 'score': result['item_score']})
                    except Item.DoesNotExist:
                        pass
        except IOError:    
            error_message = 'There is a problem with the search, please try again later.'
        
    return render_to_response('search/view_search_results.html', 
                              {'results': results, 
                               'search_string': search_string,
                               'error_message': error_message},
                              context_instance=RequestContext(request))