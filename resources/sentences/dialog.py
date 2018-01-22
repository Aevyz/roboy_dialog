from random import choice

empty_hand = ["do you need a beer?", "hey, want some beer?","where is your beer?"]
coffee = ["stop drinking coffe, have a beer",""]
beer = ["enjoy your beer"] 
intro = ["I am Roby, who are you?", ""]


sentences = {"beer" : beer, "coffe" : coffee, "intro" : intro, "empty hand" : empty_hand }



def say(arg) :
  print choice(arg)
 


#def conversation():



if __name__ == "__main__" :
  say(sentences["empty hand"])
  	

