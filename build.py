#!/usr/bin/env python
"""
This script is used to create necessary archive files to be uploaded to the samples repository of
developer.adroitlogic.com
"""

BASE_VERSION = "21.01"

import os
import re
import shutil
from contextlib import closing
from zipfile import ZipFile, ZIP_DEFLATED

__author__ = "Sajith Dilshan"
__email__ = "sajith@adroitlogic.com"

# use XS_IGNORE_PATTERNS env variable to specify space separated list of filename patterns (fragments) to be ignored
ignore_patterns = os.environ.get("XS_IGNORE_PATTERNS", "-json.xpos license.properties license.key.properties client.key.properties").split(" ")

def zip_directory(basedir, archive_name):
    """
    This method includes all the files/directories under basedir into the zip file

    :param basedir: Path of the base directory where its content should be zipped
    :param archive_name: Path of the archive file with its name

    """
    assert os.path.isdir(basedir)
    with closing(ZipFile(archive_name, "w", ZIP_DEFLATED)) as zipFile:
        print "Creating Archive : " + archive_name
        for root, dirs, files in os.walk(basedir):
            all_parts = re.split(r"[/\\]", root)
            parts = all_parts[:2]
            if ".idea" in parts or "target" in parts: continue

            for file in files:
                if all_parts[-1] == "logs" and file != ".dirinfo": continue
                if file.endswith(".iml"): continue

                absolute_file_path = os.path.join(root, file)

                if any(absolute_file_path.find(f) > -1 for f in ignore_patterns):
                    print "Ignoring         : %s" % absolute_file_path
                    continue

                # removing the basedir from absolute_file_path name so the zip file will not
                # contain a directory with the name of basedir
                zip_file_path = absolute_file_path[len(basedir) + len(os.sep):]
                zipFile.write(absolute_file_path, zip_file_path)


def recursively_delete_files(path, names):
    """
    This method will recursively delete files matching the names in the "names" tuple under th
    specified path

    :param path: Path to be scanned to delete files
    :param names: Tuple of names containing files to be deletes

    """
    for root, dirs, files in os.walk(path):
        for currentFile in files:
            if currentFile in names:
                absolute_file_path = os.path.join(root, currentFile)
                print "Removing File : " + absolute_file_path
                os.remove(absolute_file_path)


def read_file_to_string(file_path):
    """
    Utility method to read file into a string

    :param file_path: Path of the source file
    :return: the content of the file as a String

    """
    with open(file_path, 'r') as content_file:
        return content_file.read()


def create_directory(path):
    """
    Utility  method to creat a directory if it does not exist

    :param path: Path of the target directory

    """
    if not os.path.exists(path):
        os.makedirs(path)


def write_to_file(file_path, content):
    """

    Utility method to write a string to a file
    :param file_path: Path of the target file
    :param content: String containing content to be written to the file

    """
    with open(file_path, "w") as content_file:
        content_file.write(content)


def main():
    """
    Directory list to be excluded when scanning for samples
    """
    escaped_dirs = ['.idea', '.git', 'target']

    """
    List of samples directories
    """
    available_dirs = next(os.walk('.'))[1]

    """
    Modified POM content to be used for samples in the UltraStudio
    """
    mod_pom = """<modelVersion>4.0.0</modelVersion>

        <groupId>${groupId}</groupId>
        <artifactId>${artifactID}</artifactId>
        <version>${version}</version>
        <packaging>xpr</packaging>"""

    # delete the target directory if it already exists
    if os.path.exists("./target"):
        shutil.rmtree('./target')
    # create the target directory
    create_directory("./target")
    # create the target/ide directory
    create_directory("./target/ide")

    for dir in available_dirs:
        if dir not in escaped_dirs:
            # remove .DS_Store files
            recursively_delete_files("./" + dir, ".DS_Store")

            # fix base.version
            original_pom_content = read_file_to_string(dir + "/pom.xml")
            portable_pom = original_pom_content.replace("${base.version}", BASE_VERSION)
            write_to_file("./" + dir + "/pom.xml", portable_pom)

            # compress the samples
            zip_directory(dir, "./target/" + dir + ".zip")

            # modify the pom.xml content
            modified_pom_content = re.sub("<modelVersion>.*</packaging>", mod_pom, portable_pom,
                                          flags=re.DOTALL)
            write_to_file("./" + dir + "/pom.xml", modified_pom_content)
            # create archived to be used in UltraStudio
            zip_directory(dir, "./target/ide/" + dir + ".zip")

            # revert changes made to pom.xml
            write_to_file("./" + dir + "/pom.xml", original_pom_content)


if __name__ == '__main__': main()
