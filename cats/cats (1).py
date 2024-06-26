"""Typing test implementation"""

from utils import *
from ucb import main, interact, trace
from datetime import datetime


###########
# Phase 1 #
###########


def choose(paragraphs, select, k):
    """Return the Kth paragraph from PARAGRAPHS for which SELECT called on the
    paragraph returns true. If there are fewer than K such paragraphs, return
    the empty string.
    """
    # BEGIN PROBLEM 1
    acceptable_paragraphs = []
    for paragraph in paragraphs:
        if select(paragraph):
            acceptable_paragraphs.append(paragraph)
    if k>=len(acceptable_paragraphs):
        return ''
    else:
        return acceptable_paragraphs[k]
    # END PROBLEM 1


def about(topic):
    """Return a select function that returns whether a paragraph contains one
    of the words in TOPIC.

    >>> about_dogs = about(['dog', 'dogs', 'pup', 'puppy'])
    >>> choose(['Cute Dog!', 'That is a cat.', 'Nice pup!'], about_dogs, 0)
    'Cute Dog!'
    >>> choose(['Cute Dog!', 'That is a cat.', 'Nice pup.'], about_dogs, 1)
    'Nice pup.'
    """
    assert all([lower(x) == x for x in topic]), 'topics should be lowercase.'
    # BEGIN PROBLEM 2
    def does_it_have(paragraphs):
        remove_punc_par,lower_case_par,split_par = [],[],[]
        for paragraph in paragraphs:
            remove_punc_par.append(remove_punctuation(str(paragraphs)))
        for string in remove_punc_par:
            lower_case_par.append(lower(string))
        for sentence in lower_case_par:
            split_par = split(sentence)
            for word in topic:
                if word in split_par:
                    return True
        return False
    return does_it_have
    # END PROBLEM 2


def accuracy(typed, reference):
    """Return the accuracy (percentage of words typed correctly) of TYPED
    when compared to the prefix of REFERENCE that was typed.

    >>> accuracy('Cute Dog!', 'Cute Dog.')
    50.0
    >>> accuracy('A Cute Dog!', 'Cute Dog.')
    0.0
    >>> accuracy('cute Dog.', 'Cute Dog.')
    50.0
    >>> accuracy('Cute Dog. I say!', 'Cute Dog.')
    50.0
    >>> accuracy('Cute', 'Cute Dog.')
    100.0
    >>> accuracy('', 'Cute Dog.')
    0.0
    """
    typed_words = split(typed)
    reference_words = split(reference)
    # BEGIN PROBLEM 3
    accurate_words,element = [],0
    if min((len(typed_words),len(reference_words)))==0:
        return 0.0
    while element<len(reference_words) and element<len(typed_words):
        if reference_words[element]==typed_words[element]:
            accurate_words.append(typed_words[element])
        element+=1
    return (len(accurate_words)/len(typed_words))*100
    # END PROBLEM 3


def wpm(typed, elapsed):
    """Return the words-per-minute (WPM) of the TYPED string."""
    assert elapsed > 0, 'Elapsed time must be positive'
    # BEGIN PROBLEM 4
    actual_words = len(typed)/5
    return actual_words/((elapsed)/60)

    # END PROBLEM 4


def autocorrect(user_word, valid_words, diff_function, limit):
    """Returns the element of VALID_WORDS that has the smallest difference
    from USER_WORD. Instead returns USER_WORD if that difference is greater
    than LIMIT.
    """
    # BEGIN PROBLEM 5
    difference_of_words = []
    for word in valid_words:
        difference_of_words.append(diff_function(user_word,word,limit))
    min_word = min(difference_of_words)
    if user_word in valid_words or min_word>limit:
        return user_word
    for element in range(len(difference_of_words)):
        if difference_of_words[element]==min_word:
            return valid_words[element]
    # END PROBLEM 5


def sphinx_swap(start, goal, limit):
    """A diff function for autocorrect that determines how many letters
    in START need to be substituted to create GOAL, then adds the difference in
    their lengths.
    """
    # BEGIN PROBLEM 6
    def element(index,length):
        if min(len(start),len(goal))<=index:
            return length + abs(len(start)-len(goal))
        if start[index]!=goal[index]:
            return element(index+1,length+1)
        elif length>limit:
            return True
        return element(index+1,length)
    return element(0,0)
    # END PROBLEM 6


def feline_fixes(start, goal, limit):
    """A diff function that computes the edit distance from START to GOAL."""
    if len(start)==0: # Fill in the condition
        # BEGIN
        return len(goal)
        # END
    elif (limit<0):
        return 1
    elif len(goal)==0: # Feel free to remove or add additional cases
        # BEGIN
        return len(start)
        # END
    elif start[0]==goal[0]:
        return feline_fixes(start[1:],goal[1:],limit)
    else:
        add_diff = 1 + feline_fixes(start,goal[1:],limit-1)
        remove_diff = 1 + feline_fixes(start[1:],goal,limit-1)
        substitute_diff = 1 + feline_fixes(start[1:],goal[1:],limit-1)
        # BEGIN
        return min(add_diff,remove_diff,substitute_diff)
        # END


def final_diff(start, goal, limit):
    """A diff function. If you implement this function, it will be used."""
    assert False, 'Remove this line to use your final_diff function'


###########
# Phase 3 #
###########


def report_progress(typed, prompt, id, send):
    """Send a report of your id and progress so far to the multiplayer server."""
    # BEGIN PROBLEM 8
    index, progress = 0, 0
    while index<len(typed):
        if typed[index]==prompt[index]:
            progress+=1
        else:
            break
        index+=1
    send({'id': id, 'progress': progress/len(prompt)})
    return progress/len(prompt)
    # END PROBLEM 8


def fastest_words_report(times_per_player, words):
    """Return a text description of the fastest words typed by each player."""
    game = time_per_word(times_per_player, words)
    fastest = fastest_words(game)
    report = ''
    for i in range(len(fastest)):
        words = ','.join(fastest[i])
        report += 'Player {} typed these fastest: {}\n'.format(i + 1, words)
    return report


def time_per_word(times_per_player, words):
    """Given timing data, return a game data abstraction, which contains a list
    of words and the amount of time each player took to type each word.

    Arguments:
        times_per_player: A list of lists of timestamps including the time
                          the player started typing, followed by the time
                          the player finished typing each word.
        words: a list of words, in the order they are typed.
    """
    # BEGIN PROBLEM 9
    times = []
    player=0
    while player<len(times_per_player):
        times.append([(times_per_player[player][i+1]- times_per_player[player][i]) for i in range(0,len(times_per_player[0])-1)])
        player+=1
    return game(words,times)
    # END PROBLEM 9


def fastest_words(game):
    """Return a list of lists of which words each player typed fastest.

    Arguments:
        game: a game data abstraction as returned by time_per_word.
    Returns:
        a list of lists containing which words each player typed fastest
    """
    players = range(len(all_times(game)))  # An index for each player
    words = range(len(all_words(game)))    # An index for each word
    # BEGIN PROBLEM 10
    #game(['What', 'great', 'luck'], [p0, p1])
    fastest_words_of_players = []
    for player in players:
        fastest_words_of_players  = fastest_words_of_players + [[]]
    for word in words:
        fastest = 0
        for player in players:
            if time(game,player,word)<time(game,fastest,word):
                fastest = player
        fastest_words_of_players[fastest]= fastest_words_of_players[fastest] + [word_at(game,word)]
    return fastest_words_of_players 


    # END PROBLEM 10


def game(words, times):
    """A data abstraction containing all words typed and their times."""
    assert all([type(w) == str for w in words]), 'words should be a list of strings'
    assert all([type(t) == list for t in times]), 'times should be a list of lists'
    assert all([isinstance(i, (int, float)) for t in times for i in t]), 'times lists should contain numbers'
    assert all([len(t) == len(words) for t in times]), 'There should be one word per time.'
    return [words, times]


def word_at(game, word_index):
    """A selector function that gets the word with index word_index"""
    assert 0 <= word_index < len(game[0]), "word_index out of range of words"
    return game[0][word_index]


def all_words(game):
    """A selector function for all the words in the game"""
    return game[0]


def all_times(game):
    """A selector function for all typing times for all players"""
    return game[1]


def time(game, player_num, word_index):
    """A selector function for the time it took player_num to type the word at word_index"""
    assert word_index < len(game[0]), "word_index out of range of words"
    assert player_num < len(game[1]), "player_num out of range of players"
    return game[1][player_num][word_index]


def game_string(game):
    """A helper function that takes in a game object and returns a string representation of it"""
    return "game(%s, %s)" % (game[0], game[1])

enable_multiplayer = False  # Change to True when you


##########################
# Command Line Interface #
##########################


def run_typing_test(topics):
    """Measure typing speed and accuracy on the command line."""
    paragraphs = lines_from_file('data/sample_paragraphs.txt')
    select = lambda p: True
    if topics:
        select = about(topics)
    i = 0
    while True:
        reference = choose(paragraphs, select, i)
        if not reference:
            print('No more paragraphs about', topics, 'are available.')
            return
        print('Type the following paragraph and then press enter/return.')
        print('If you only type part of it, you will be scored only on that part.\n')
        print(reference)
        print()

        start = datetime.now()
        typed = input()
        if not typed:
            print('Goodbye.')
            return
        print()

        elapsed = (datetime.now() - start).total_seconds()
        print("Nice work!")
        print('Words per minute:', wpm(typed, elapsed))
        print('Accuracy:        ', accuracy(typed, reference))

        print('\nPress enter/return for the next paragraph or type q to quit.')
        if input().strip() == 'q':
            return
        i += 1


@main
def run(*args):
    """Read in the command-line argument and calls corresponding functions."""
    import argparse
    parser = argparse.ArgumentParser(description="Typing Test")
    parser.add_argument('topic', help="Topic word", nargs='*')
    parser.add_argument('-t', help="Run typing test", action='store_true')

    args = parser.parse_args()
    if args.t:
        run_typing_test(args.topic)
