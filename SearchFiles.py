#!/usr/bin/env python
# -*- coding: utf-8 -*-
#===============================================================================
#
# Copyright (c) 2017 <> All Rights Reserved
#
#
# File: /Users/hain/git/pylucene-get-started/foo.py
# Author: Hai Liang Wang
# Date: 2017-10-26:08:24:38
#
#===============================================================================

"""
   
"""
from __future__ import print_function
from __future__ import division

__copyright__ = "Copyright (c) 2017 . All Rights Reserved"
__author__    = "Hai Liang Wang"
__date__      = "2017-10-26:08:24:38"




import sys, os, lucene
curdir = os.path.dirname(os.path.abspath(__file__))
sys.path.append(curdir)

INDEX_DIR = os.path.join(curdir, "index_sample")

from java.nio.file import Paths
from org.apache.lucene.analysis.core import WhitespaceAnalyzer
from org.apache.lucene.index import DirectoryReader
from org.apache.lucene.queryparser.classic import QueryParser
from org.apache.lucene.store import SimpleFSDirectory
from org.apache.lucene.search import IndexSearcher

"""
This script is loosely based on the Lucene (java implementation) demo class
org.apache.lucene.demo.SearchFiles.  It will prompt for a search query, then it
will search the Lucene index in the current directory called 'index' for the
search query entered against the 'contents' field.  It will then display the
'path' and 'name' fields for each of the hits it finds in the index.  Note that
search.close() is currently commented out because it causes a stack overflow in
some cases.
"""
def run(searcher, analyzer):
    while True:
        print("Hit enter with no input to quit.")
        command = input("Query:")
        if command == '':
            return

        print("Searching for:", command)
        query = QueryParser("contents", analyzer).parse(command)
        scoreDocs = searcher.search(query, 50).scoreDocs
        # scoreDocs = searcher.explain(query, 50).scoreDocs
        print("%s total matching documents." % len(scoreDocs))

        # for o in searcher.explain(query, 50).details:
        #     print(o)

        for scoreDoc in scoreDocs:
            doc = searcher.doc(scoreDoc.doc)
            print('id:', doc.get("id"), 'score:', scoreDoc.score, 'post:', doc.get("post"))


if __name__ == '__main__':
    lucene.initVM(vmargs=['-Djava.awt.headless=true'])
    print('lucene', lucene.VERSION)
    base_dir = os.path.dirname(os.path.abspath(sys.argv[0]))
    directory = SimpleFSDirectory(Paths.get(os.path.join(base_dir, INDEX_DIR)))
    searcher = IndexSearcher(DirectoryReader.open(directory))
    analyzer = WhitespaceAnalyzer()
    run(searcher, analyzer)
    del searcher
