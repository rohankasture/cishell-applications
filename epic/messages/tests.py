from django.test import TestCase

from django.contrib.auth.models import User
from epic.messages.models import ReceivedMessage, SentMessage

class ViewTests(TestCase):
	fixtures = ['initial_data']
	
	
	
	def setUp(self):
		pass
	
	def tearDown(self):
		pass
	
	def testIndex(self):
		admin = User.objects.get(username="admin")
		peebs   = User.objects.get(username="peebs")
	
		m1r = ReceivedMessage.objects.create(recipient=peebs, sender=admin, subject="m1r", message="this is the first received message", read=False, replied=False, deleted=False)
		m1s = SentMessage.objects.create(recipient=peebs, sender=admin, subject="m1s", message="this is the first sent message", deleted=False)
		m2r = ReceivedMessage.objects.create(recipient=admin, sender=admin, subject="m2r", message="this is the second received message", read=False, replied=False, deleted=False)
		m2s = SentMessage.objects.create(recipient=admin, sender=admin, subject="m2s", message="this is the second sent message", deleted=False)
		# Verify that login is required
		response = self.client.get('/user/%s/messages/' % (admin.id))
		self.assertEqual(response.status_code, 302)
		# Log in as peebs and check the situation
		login = self.client.login(username='peebs', password='map')
		self.failUnless(login, 'Could not login as peebs')
		response = self.client.get('/user/%s/messages/' % (peebs.id))
		self.assertEqual(response.status_code, 200)
		# Peebs can only see m1r, none of the ohters
		self.assertFalse('<a href="/user/%s/messages/sent/%s/">%s</a>' % (peebs.id, m1s.id, m1s.subject) in response.content, response.content)
		self.assertFalse('<a href="/user/%s/messages/sent/%s/">%s</a>' % (peebs.id, m2s.id, m2s.subject) in response.content, response.content)
		self.assertTrue('<a href="/user/%s/messages/received/%s/">%s</a>' % (peebs.id, m1r.id, m1r.subject) in response.content, response.content)
		self.assertFalse('<a href="/user/%s/messages/received/%s/">%s</a>' % (peebs.id, m2r.id, m2r.subject) in response.content, response.content)
		
		# log in as admin 
		login = self.client.login(username='admin', password='admin')
		self.failUnless(login, 'Could not login as admin')
		response = self.client.get('/user/%s/messages/' % (admin.id))
		# admin should see the opposite of peebs, that is only m1r is not visable.
		self.assertTrue('<a href="/user/%s/messages/sent/%s/">%s</a>' % (admin.id, m1s.id, m1s.subject) in response.content, response.content)
		self.assertTrue('<a href="/user/%s/messages/sent/%s/">%s</a>' % (admin.id, m2s.id, m2s.subject) in response.content, response.content)
		self.assertFalse('<a href="/user/%s/messages/received/%s/">%s</a>' % (admin.id, m1r.id, m1r.subject) in response.content, response.content)
		self.assertTrue('<a href="/user/%s/messages/received/%s/">%s</a>' % (admin.id, m2r.id, m2r.subject) in response.content, response.content)
	
	def testSendMessage(self):
		admin = User.objects.get(username="admin")
		peebs   = User.objects.get(username="peebs")
		
		# Verify that login is required
		response = self.client.get('/user/%s/messages/' % (admin.id))
		self.assertEqual(response.status_code, 302)
		# Log in
		login = self.client.login(username='peebs', password='map')
		self.failUnless(login, 'Could not login')
		# Goto create new message page
		response = self.client.get('/user/%s/messages/new/' % (peebs.id))
		self.assertEqual(response.status_code, 200)
		self.assertTrue('Send a new message' in response.content)
		# test that you can't have a blank recipient field 
		post_data = {
					 'recipient': '',
                     'subject': 'm3',
                     'message': 'This is message number 3',
        }
		response = self.client.post('/user/%s/messages/new/' % (peebs.id), post_data)
		self.failUnlessEqual(response.status_code, 200)
		self.failUnless('required' in response.content)
		# Verify that posting an invalid user returns an error
		post_data = {
					 'recipient': 'asdf 43qwt sdg ds',
                     'subject': 'm3',
                     'message': 'This is message number 3',
        }
		response = self.client.post('/user/%s/messages/new/' % (peebs.id), post_data)
		self.failUnlessEqual(response.status_code, 200)
		self.failUnless('asdf 43qwt sdg ds is not a valid username' in response.content)
		# Post a valid message with admin as the recipient
		post_data = {
					 'recipient': 'admin',
                     'subject': 'm3',
                     'message': 'This is message number 3',
        }
		response = self.client.post('/user/%s/messages/new/' % (peebs.id), post_data)
		self.failUnlessEqual(response.status_code, 302)

		# Log in as admin and check for the new message
		login = self.client.login(username='admin', password='admin')
		self.failUnless(login, 'Could not login')
		response = self.client.get('/user/%s/messages/' % (admin.id))
		self.assertEqual(response.status_code, 200)
		self.assertFalse('m1' in response.content, response.content)
		self.assertFalse('m2' in response.content, response.content)
		self.assertTrue('m3' in response.content, response.content)
	
	def testViewMessage(self):
		admin = User.objects.get(username="admin")
		peebs   = User.objects.get(username="peebs")
	
		m1r = ReceivedMessage.objects.create(recipient=peebs, sender=admin, subject="m1r", message="this is the first received message", read=False, replied=False, deleted=False)
		m1s = SentMessage.objects.create(recipient=peebs, sender=admin, subject="m1s", message="this is the first sent message", deleted=False)
		m2r = ReceivedMessage.objects.create(recipient=admin, sender=admin, subject="m2r", message="this is the second received message", read=False, replied=False, deleted=False)
		m2s = SentMessage.objects.create(recipient=admin, sender=admin, subject="m2s", message="this is the second sent message", deleted=False)
		
		# Make sure that you must be logged in to view.
		response = self.client.get('/user/%s/messages/' % (peebs.id))
		self.assertEqual(response.status_code, 302)
		# Make sure that you can see allowed received messages if you are logged in
		login = self.client.login(username='admin', password='admin')
		self.failUnless(login, 'Could not login')
		response = self.client.get('/user/%s/messages/received/%s/' % (admin.id, m2r.id))
		self.failUnlessEqual(response.status_code, 200)
		self.assertTrue('this is the second received message' in response.content)
		# Verify that someone who did not send or receive the message can't see it
		login = self.client.login(username='peebs', password='map')
		self.failUnless(login, 'Could not login')
		response = self.client.get('/user/%s/messages/received/%s/' % (peebs.id, m2r.id))
		#TODO: when using upgraded django, follow this redirect and verify you are sent the right place
		self.failUnlessEqual(response.status_code, 302)
		
		# Make sure that you can see allowed sent messages if you are logged in
		login = self.client.login(username='admin', password='admin')
		self.failUnless(login, 'Could not login')
		response = self.client.get('/user/%s/messages/sent/%s/' % (admin.id, m2s.id))
		self.failUnlessEqual(response.status_code, 200)
		self.assertTrue('this is the second sent message' in response.content)
		# Verify that someone who did not send or receive the message can't see it
		login = self.client.login(username='peebs', password='map')
		self.failUnless(login, 'Could not login')
		response = self.client.get('/user/%s/messages/sent/%s/' % (peebs.id, m2s.id))
		#TODO: when using upgraded django, follow this redirect and verify you are sent the right place
		self.failUnlessEqual(response.status_code, 302)
		